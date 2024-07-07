package com.dilshan.productservice.core.errorhandling;


import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

import javax.annotation.Nonnull;

@Slf4j
public class ProductsServiceEventsErrorHandler implements ListenerInvocationErrorHandler {

    @Override
    public void onError(@Nonnull Exception exception, @Nonnull EventMessage<?> event,
                        @Nonnull EventMessageHandler eventHandler) throws Exception {
        log.error("Error occurred while handling event: {} with handler: {}", event.getPayloadType(), eventHandler, exception);
        throw exception;
    }
}