package com.broker.social_companion_system.global_services;

import com.broker.social_companion_system.client.ClientNotification;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.operator.OperatorNotification;
import com.broker.social_companion_system.server.ServerNotification;
import com.broker.social_companion_system.server.ServerNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReplyMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageToClient(ClientNotification notification, String client) {
        simpMessagingTemplate.convertAndSendToUser(client, "/queue/reply", notification);
    }

    public void sendMessageToAllClients(ClientNotification notification) {
        log.info("Sending msg to all clients: " + notification.clientEvent());
        simpMessagingTemplate.convertAndSend("/public/client", notification);
    }

    public void sendPackageToServer(QueryPackage queryPackage) {
        simpMessagingTemplate.convertAndSendToUser(queryPackage.requestedUnitId(), "/queue/reply",
                new ServerNotification(ServerNotificationType.QUERY, queryPackage));
    }

    public void sendMessageToOperator(OperatorNotification operatorNotification, String operator) {
        simpMessagingTemplate.convertAndSendToUser(operator, "/queue/reply", operatorNotification);
    }

    public void sendMessageToAllOperators(OperatorNotification operatorNotification) {
        log.info("Sending to all operators: " + operatorNotification.operatorEvent());
        simpMessagingTemplate.convertAndSend("/public/operator", operatorNotification);
    }

}
