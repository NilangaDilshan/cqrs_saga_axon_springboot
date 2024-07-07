package com.dilshan.productservice.command.api.events.handler;

import com.dilshan.productservice.core.data.model.ProductLookupEntity;
import com.dilshan.productservice.core.data.repository.ProductLookupEntityRepository;
import com.dilshan.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@Slf4j
@RequiredArgsConstructor
public class ProductsLookupEventsHandler {

    private final ProductLookupEntityRepository productLookupEntityRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        log.info("ProductCreatedEvent: {}", event.getProductId());
        this.productLookupEntityRepository.save(new ProductLookupEntity(event.getProductId(), event.getTitle()));
    }

    @ResetHandler
    public void reset() {
        log.info("Resetting the lookup table");
        this.productLookupEntityRepository.deleteAll();
    }
}
