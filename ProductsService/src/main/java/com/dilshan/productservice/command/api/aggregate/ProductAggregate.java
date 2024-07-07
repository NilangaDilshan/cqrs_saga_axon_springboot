package com.dilshan.productservice.command.api.aggregate;

import com.dilshan.core.commands.CancelProductReservationCommand;
import com.dilshan.core.commands.ReserveProductCommand;
import com.dilshan.core.events.ProductReservationCancelledEvent;
import com.dilshan.core.events.ProductReservedEvent;
import com.dilshan.productservice.command.api.commands.CreateProductCommand;
import com.dilshan.productservice.core.events.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
@Slf4j
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate() {
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        log.info("CommandHandler: {}", createProductCommand.getProductId());
        var productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        AggregateLifecycle.apply(productCreatedEvent);
    }

    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {
        log.info("ReserveProductCommand: {}", reserveProductCommand.getProductId());
        if (this.quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }
        var productReservedEvent = ProductReservedEvent.builder()
                .productId(reserveProductCommand.getProductId())
                .orderId(reserveProductCommand.getOrderId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();
        AggregateLifecycle.apply(productReservedEvent);
    }

    @CommandHandler
    public void on(CancelProductReservationCommand cancelProductReservationCommand) {
        log.info("CancelProductReservationCommand: {}", cancelProductReservationCommand.getProductId());
        var productReservedEvent = ProductReservationCancelledEvent.builder()
                .productId(cancelProductReservationCommand.getProductId())
                .orderId(cancelProductReservationCommand.getOrderId())
                .quantity(cancelProductReservationCommand.getQuantity())
                .userId(cancelProductReservationCommand.getUserId())
                .reason(cancelProductReservationCommand.getReason())
                .build();
        AggregateLifecycle.apply(productReservedEvent);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        this.productId = productCreatedEvent.getProductId();
        this.title = productCreatedEvent.getTitle();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        this.quantity -= productReservedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent productReservationCancelEvent) {
        this.quantity += productReservationCancelEvent.getQuantity();
    }
}
