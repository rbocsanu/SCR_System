package com.broker.social_companion_system.server;

import com.broker.social_companion_system.client.QueryDto;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.common_dtos.ResponseDto;
import com.broker.social_companion_system.entities.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
@MessageMapping("/server")
public class ServerController {

    private final ServerManagementService serverManagementService;

    @MessageMapping("/init_server_connection")
    @SendToUser("/queue/response")
    public ResponseDto<?> initServerConnection() {
        String server = SecurityContextHolder.getContext().getAuthentication().getName();
        serverManagementService.serverJoined(server);

        return new ResponseDto<>(HttpStatus.OK);
    }

    @MessageMapping("/started_query")
    @SendToUser("/queue/response")
    public ResponseDto<?> startedQuery(
            @Payload QueryPackage queryPackage
    ) {
        log.info("Started Query: " + queryPackage);

        return new ResponseDto<>(HttpStatus.OK);
    }

    @MessageMapping("/completed_query")
    @SendToUser("/queue/response")
    public ResponseDto<?> completedQuery(
            @Payload QueryPackage queryPackage
            ) {
        log.info("Completed query: " + queryPackage);
        serverManagementService.serverCompletedQuery(queryPackage);

        return new ResponseDto<>(HttpStatus.OK);
    }

}
