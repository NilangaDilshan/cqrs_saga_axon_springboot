package com.dilshan.orderservice.core.events;

import com.dilshan.orderservice.core.data.model.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    private final String orderId;

    private final OrderStatus orderStatus;
}
