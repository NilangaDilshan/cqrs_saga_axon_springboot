package com.dilshan.paymentservice.query.api.events.handler;

import com.dilshan.core.events.PaymentProcessedEvent;
import com.dilshan.paymentservice.core.data.model.PaymentEntity;
import com.dilshan.paymentservice.core.data.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventsHandler {

    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        log.info("PaymentProcessedEvent: {}", event.getPaymentId());
        this.paymentRepository.save(new PaymentEntity(event.getPaymentId(), event.getOrderId()));
    }

}
