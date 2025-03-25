package com.broker.social_companion_system.client;

import com.broker.social_companion_system.common_dtos.ResponseDto;
import com.broker.social_companion_system.global_services.QueueManager;
import com.broker.social_companion_system.global_services.RequestType;
import com.broker.social_companion_system.global_services.VisualizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
@MessageMapping("/client")
public class ClientController {

    //private static final Logger log = LoggerFactory.getLogger(BrokerApplication.class);
    private final ClientQueryService clientQueryService;
    private final VisualizeService visualizeService;
    private final QueueManager queueManager;

    @MessageMapping("/init_client_connection")
    public ResponseDto<ClientNotification> initClientConnection() {
        String client = SecurityContextHolder.getContext().getAuthentication().getName();
        visualizeService.publishVisual(RequestType.CLIENT_CONNECTED, "Client " + client);
        return new ResponseDto<>(HttpStatus.OK, new ClientNotification(ClientEvent.ADD_ALL_UNITS,
                queueManager.getAvailableServers().toArray(new String[0])));
    }

    @MessageMapping("/request_query")
    @SendToUser("/queue/responses")
    public ResponseDto<ClientNotification> requestQuery(
            @Payload QueryDto query
    ) {
        log.info("Received send query: " + query);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Client username: " + user);

        boolean approved = clientQueryService.handleQueryRequest(query.message(), query.requestedUnitId(), user);

        ClientNotification clientNotification = approved
                ? new ClientNotification(ClientEvent.ADD_SCHEDULED, new String[]{query.message()})
                : new ClientNotification(ClientEvent.ADD_PENDING, new String[]{query.message()});

        return new ResponseDto<>(HttpStatus.OK, clientNotification);
    }

    @MessageMapping("/disconnect_client")
    @SendToUser("/queue/responses")
    public ResponseDto<?> disconnectClient() {
        log.info("Received send request");
        return new ResponseDto<>(HttpStatus.OK);
    }

}
