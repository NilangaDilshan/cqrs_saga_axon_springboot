package com.dilshan.orderservice.query.api.projection;

import com.dilshan.orderservice.core.data.model.OrderEntity;
import com.dilshan.orderservice.core.data.repository.OrdersRepository;
import com.dilshan.orderservice.core.model.OrderSummary;
import com.dilshan.orderservice.query.api.queries.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderProjection {

    private final OrdersRepository orderRepository;

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery query) {
        log.info("Find Order by ID: {}", query.getOrderId());
        return this.orderRepository.findByOrderId(query.getOrderId())
                .map(orderEntity -> {
                    log.info("Order found: {}", orderEntity.getOrderId());
                    return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
                })
                .orElseGet(() -> {
                    log.warn("Order not found: {}", query.getOrderId());
                    return null;
                });
    }
}
