package com.dilshan.orderservice.core.data.repository;

import com.dilshan.orderservice.core.data.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, String> {
    Optional<OrderEntity> findByOrderId(String orderId);
}
