package com.dilshan.orderservice.core.model;

import com.dilshan.orderservice.core.data.model.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {
    private final String orderId;
    private final OrderStatus orderStatus;
    private final String reason;
}
