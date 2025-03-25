package com.broker.social_companion_system.global_services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisualizeService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${visualize}")
    public boolean visualize;

    public void publishVisual(RequestType requestType, String event) {

        Map<String, Object> map = Map.of("requestType", requestType, "event", event);

        log.info("Visualize: " + visualize);

        if (visualize) {
            simpMessagingTemplate.convertAndSendToUser("visualization", "/queue/reply", map);
        }
    }

}
