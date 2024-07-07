package com.dilshan.orderservice.query.api.queries;

import lombok.Value;

@Value
public class FindOrderQuery {
    private final String orderId;
}
