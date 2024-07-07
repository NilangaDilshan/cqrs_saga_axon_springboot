package com.dilshan.paymentservice.command.api.aggregate;

import com.dilshan.core.commands.ProcessPaymentCommand;
import com.dilshan.core.events.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    
    public PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        log.info("PaymentAggregate ProcessPaymentCommand: {}", processPaymentCommand.toString());
        if (processPaymentCommand == null) {
            throw new IllegalArgumentException("ProcessPaymentCommand must not be null");
        }
        if (processPaymentCommand.getPaymentId() == null || processPaymentCommand.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("PaymentId must not be null or empty");
        }
        if (processPaymentCommand.getOrderId() == null || processPaymentCommand.getOrderId().isBlank()) {
            throw new IllegalArgumentException("OrderId must not be null or empty");
        }
        if (processPaymentCommand.getPaymentDetails() == null) {
            throw new IllegalArgumentException("PaymentDetails must not be null");
        }
        AggregateLifecycle.apply(new PaymentProcessedEvent(processPaymentCommand.getOrderId(),
                processPaymentCommand.getPaymentId()));
    }

    @EventSourcingHandler
    protected void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
    }

}
