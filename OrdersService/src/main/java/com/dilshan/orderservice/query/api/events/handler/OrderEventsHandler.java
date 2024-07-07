package com.dilshan.orderservice.query.api.events.handler;

import com.dilshan.orderservice.core.data.model.OrderEntity;
import com.dilshan.orderservice.core.data.model.OrderStatus;
import com.dilshan.orderservice.core.data.repository.OrdersRepository;
import com.dilshan.orderservice.core.events.OrderApprovedEvent;
import com.dilshan.orderservice.core.events.OrderCreatedEvent;
import com.dilshan.orderservice.core.events.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ProcessingGroup("order-group")
public class OrderEventsHandler {

    private final OrdersRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Order created event received: {}", event.toString());
        var orderEntity = new OrderEntity(event.getOrderId(), event.getProductId(),
                event.getUserId(), event.getQuantity(), event.getAddressId(), event.getOrderStatus());
        this.orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        log.info("Order approved event received: {}", event.toString());
        this.orderRepository.findByOrderId(event.getOrderId()).ifPresent(orderEntity -> {
            orderEntity.setOrderStatus(event.getOrderStatus());
            this.orderRepository.save(orderEntity);
        });
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {
        log.info("Order rejected event received: {}", event.toString());
        this.orderRepository.findByOrderId(event.getOrderId()).ifPresent(orderEntity -> {
            orderEntity.setOrderStatus(OrderStatus.REJECTED);
            this.orderRepository.save(orderEntity);
        });
    }

}
