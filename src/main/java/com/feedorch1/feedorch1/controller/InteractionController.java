package com.feedorch1.feedorch1.controller;

import com.feedorch1.feedorch1.dto.StoryInteractionEvent;
import com.feedorch1.feedorch1.config.RabbitMQConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interaction")
@RequiredArgsConstructor
public class InteractionController {
    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public ResponseEntity<Map<String,String>> recordInteraction(@Valid @RequestBody StoryInteractionEvent event) {
        //drop the payload into rabbit
        rabbitTemplate.convertAndSend(RabbitMQConfig.INTERACTION_EXCHANGE, RabbitMQConfig.INTERACTION_ROUTING_KEY, event);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "telemetry signal queued"));
    }
}
