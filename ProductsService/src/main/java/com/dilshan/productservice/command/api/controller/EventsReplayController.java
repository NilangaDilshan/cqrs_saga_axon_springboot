package com.dilshan.productservice.command.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/management")
@Slf4j
@RequiredArgsConstructor
public class EventsReplayController {

    private final EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventsProcessor/{processorName}/reset")
    public ResponseEntity<String> replayEvents(@PathVariable String processorName) {
        log.info("Replaying events for processor: {}", processorName);
        return this.eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class)
                .map(trackingEventProcessor -> {
                    trackingEventProcessor.shutDown();
                    trackingEventProcessor.resetTokens();
                    trackingEventProcessor.start();
                    log.info("Events replayed successfully for processor: {}", processorName);
                    return ResponseEntity.ok().body("Events replayed successfully for processor: %s".formatted(processorName));
                })
                .orElse(ResponseEntity.badRequest().body("No event processor found with name: %s".formatted(processorName)));
    }

}
