package com.dilshan.productservice.query.api.events.handler;

import com.dilshan.core.events.ProductReservationCancelledEvent;
import com.dilshan.core.events.ProductReservedEvent;
import com.dilshan.productservice.core.data.model.ProductEntity;
import com.dilshan.productservice.core.data.repository.ProductsRepository;
import com.dilshan.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private final ProductsRepository productsRepository;

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception e) throws Exception {
        log.error("Got an error in event handler Exception handle method: ", e);
        throw e;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException e) {
        log.error("Got an error in event handler IllegalArgumentException handle method: ", e);
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        log.info("Product created event received: {}", event);
        /*var productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);*/
        try {
            this.productsRepository.save(new ProductEntity(event.getProductId(), event.getTitle(), event.getPrice(), event.getQuantity()));
        } catch (IllegalArgumentException e) {
            log.error("Error occurred while saving product: ", e);
        }
        /*if (true) throw new Exception("Forcing an exception to test error handling.");*/
    }

    @EventHandler
    public void on(ProductReservedEvent event) {
        log.info("Product reserved event received. ProductID: {} OrderID: {}", event.getProductId(), event.getOrderId());
        var productEntity = this.productsRepository.findById(event.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found for product id: %s order id: %s"
                        .formatted(event.getProductId(), event.getOrderId())));
        log.info("Product Quantity before reservation: {}", productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());
        this.productsRepository.save(productEntity);
        log.info("Product Quantity after reservation: {} reserved count: {}", productEntity.getQuantity(), event.getQuantity());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) {
        log.info("Product reservation cancel event received. ProductID: {} OrderID: {}", event.getProductId(), event.getOrderId());
        var productEntity = this.productsRepository.findByProductId(event.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found for product id: %s order id: %s"
                        .formatted(event.getProductId(), event.getOrderId())));
        log.info("Product Quantity before reservation cancellation: {}", productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() + event.getQuantity());
        this.productsRepository.save(productEntity);
        log.info("Product Quantity after reservation cancellation: {} reserved count: {}", productEntity.getQuantity(), event.getQuantity());
    }

    @ResetHandler
    public void reset() {
        log.info("Resetting the projection");
        this.productsRepository.deleteAll();
    }
}
