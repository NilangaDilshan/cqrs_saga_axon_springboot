package com.dilshan.orderservice.saga;

import com.dilshan.core.commands.CancelProductReservationCommand;
import com.dilshan.core.commands.ProcessPaymentCommand;
import com.dilshan.core.commands.ReserveProductCommand;
import com.dilshan.core.events.PaymentProcessedEvent;
import com.dilshan.core.events.ProductReservationCancelledEvent;
import com.dilshan.core.events.ProductReservedEvent;
import com.dilshan.core.model.User;
import com.dilshan.core.queries.FetchUserPaymentDetailsQuery;
import com.dilshan.orderservice.command.api.commands.ApproveOrderCommand;
import com.dilshan.orderservice.command.api.commands.RejectOrderCommand;
import com.dilshan.orderservice.core.data.model.OrderStatus;
import com.dilshan.orderservice.core.events.OrderApprovedEvent;
import com.dilshan.orderservice.core.events.OrderCreatedEvent;
import com.dilshan.orderservice.core.events.OrderRejectedEvent;
import com.dilshan.orderservice.core.model.OrderSummary;
import com.dilshan.orderservice.query.api.queries.FindOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Saga
@Slf4j
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;
    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private static final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        log.info("Order created event.  OrderID: {} ProductID: {}", orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getProductId());
        var reserveProductCommand = new ReserveProductCommand(orderCreatedEvent.getProductId(),
                orderCreatedEvent.getQuantity(), orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId());
        this.commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Failed to reserve product: {}", commandResultMessage.exceptionResult().getMessage());
                //TODO: Implement compensating transaction
                var rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(),
                        commandResultMessage.exceptionResult().getMessage());
                this.commandGateway.send(rejectOrderCommand);
            } else {
                log.info("Product reserved successfully: {}", commandResultMessage.getPayload());
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        //Process user payment
        log.info("Product reserved event. ProductID: {} OrderID: {}", productReservedEvent.getProductId(),
                productReservedEvent.getOrderId());
        var fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User userDetails;
        try {
            userDetails = this.queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class))
                    .join();
        } catch (Exception e) {
            log.error("Error occurred while fetching user payment details: {}", e.getMessage());
            this.cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (userDetails == null) {
            log.error("No user found for user id: {}", productReservedEvent.getUserId());
            this.cancelProductReservation(productReservedEvent, "No user found for user id: %s".formatted(productReservedEvent.getUserId()));
            return;
        }
        log.info("Order processing completed for order id: {} FirstName: {}", productReservedEvent.getOrderId(),
                userDetails.getFirstName());

        this.scheduleId = this.deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS), PAYMENT_PROCESSING_DEADLINE,
                productReservedEvent);

        var processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();
        String result = null;
        try {
            //result = this.commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = this.commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception e) {
            log.error("Failed to process payment for order id: {}", productReservedEvent.getOrderId(), e);
            this.cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (result == null) {
            log.info("The ProcessPaymentCommand resulted in a NULL. Initiating a compensating transaction");
            this.cancelProductReservation(productReservedEvent, "Payment processing failed with the given payment details. PaymentID: %s"
                    .formatted(processPaymentCommand.getPaymentId()));
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        log.warn("Compensating transaction for product reservation. ProductID: {} OrderID: {} Reason: {}",
                productReservedEvent.getProductId(), productReservedEvent.getOrderId(), reason);
        this.cancelDeadLine();
        var cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .productId(productReservedEvent.getProductId())
                .orderId(productReservedEvent.getOrderId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();
        this.commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        log.info("Payment processed event. PaymentID: {} OrderID: {}", event.getPaymentId(), event.getOrderId());
        this.cancelDeadLine();
        var approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
        this.commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadLine() {
        log.info("Cancelling deadline with ID: {}", this.scheduleId);
        if (null != this.scheduleId) {
            this.deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE, this.scheduleId);
            this.scheduleId = null;
        } else
            log.warn("Deadline ID is NULL. Cannot cancel deadline");
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent event) {
        log.info("Order approved event. OrderID: {}", event.getOrderId());
        //SagaLifecycle.end(); //This will end the saga lifecycle likewise the annotation @EndSaga
        this.queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(event.getOrderId(), OrderStatus.APPROVED, "Order approved successfully"));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent event) {
        log.info("Product reservation cancelled event. ProductID: {} OrderID: {}", event.getProductId(), event.getOrderId());
        var rejectOrderCommand = new RejectOrderCommand(event.getOrderId(), event.getReason());
        this.commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent event) {
        log.info("Order rejected event. OrderID: {}", event.getOrderId());
        //SagaLifecycle.end(); //This will end the saga lifecycle likewise the annotation @EndSaga
        this.queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(event.getOrderId(), OrderStatus.REJECTED, event.getReason()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent event) {
        log.info("Payment deadline expired for order id: {}", event.getOrderId());
        this.cancelProductReservation(event, "Payment deadline expired");
    }
}
