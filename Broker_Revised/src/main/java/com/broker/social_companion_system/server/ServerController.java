package com.broker.social_companion_system.server;

import com.broker.social_companion_system.client.QueryDto;
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
    public ResponseDto<String> initServerConnection() {
        System.out.println("Initing server connection");
        String server = SecurityContextHolder.getContext().getAuthentication().getName();
        serverManagementService.serverJoined(server);

        return new ResponseDto<>(HttpStatus.OK);
    }

    @MessageMapping("/update")
    @SendToUser("/queue/responses")
    public ResponseDto<?> updateNotification(
            @Payload Query query
            ) {
        return new ResponseDto<>(HttpStatus.OK);
    }
}
