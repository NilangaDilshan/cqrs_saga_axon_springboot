package com.dilshan.orderservice.core.events;

import com.dilshan.orderservice.core.data.model.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
