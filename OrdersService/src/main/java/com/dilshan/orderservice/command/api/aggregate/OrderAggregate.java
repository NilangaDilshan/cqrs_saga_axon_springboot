package com.dilshan.orderservice.command.api.aggregate;

import com.dilshan.orderservice.command.api.commands.ApproveOrderCommand;
import com.dilshan.orderservice.command.api.commands.CreateOrderCommand;
import com.dilshan.orderservice.command.api.commands.RejectOrderCommand;
import com.dilshan.orderservice.core.events.OrderApprovedEvent;
import com.dilshan.orderservice.core.events.OrderCreatedEvent;
import com.dilshan.orderservice.core.data.model.OrderStatus;
import com.dilshan.orderservice.core.events.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
public class OrderAggregate {
    @AggregateIdentifier
    public String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        log.info("CommandHandler: {}", createOrderCommand.getOrderId());
        var orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.userId = orderCreatedEvent.getUserId();
        this.productId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        log.info("CommandHandler: {}", approveOrderCommand.getOrderId());
        var orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId(), OrderStatus.APPROVED);
        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @CommandHandler
    public void on(RejectOrderCommand rejectOrderCommand) {
        log.info("EventSourcingHandler RejectedOrderCommand: {}", rejectOrderCommand.getOrderId());
        var orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());
        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        this.orderStatus = OrderStatus.REJECTED;
    }
}
