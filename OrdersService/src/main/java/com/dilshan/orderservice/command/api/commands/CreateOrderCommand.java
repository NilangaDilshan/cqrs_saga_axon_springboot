package com.dilshan.orderservice.command.api.commands;

import com.dilshan.orderservice.core.data.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;
}
