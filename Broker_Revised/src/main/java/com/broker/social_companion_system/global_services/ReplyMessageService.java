package com.broker.social_companion_system.global_services;

import com.broker.social_companion_system.client.ClientNotification;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.operator.OperatorMessageDto;
import com.broker.social_companion_system.server.ServerNotification;
import com.broker.social_companion_system.server.ServerNotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageToClient(ClientNotification notification, String client) {
        simpMessagingTemplate.convertAndSendToUser(client, "/queue/reply", notification);
    }

    public void sendPackageToServer(QueryPackage queryPackage) {
        simpMessagingTemplate.convertAndSendToUser(queryPackage.requestedUnitId(), "/queue/reply",
                new ServerNotification(ServerNotificationType.QUERY, queryPackage));
    }

    public void sendMessageToOperator(QueryPackage operatorMessageDto, String operator) {
        simpMessagingTemplate.convertAndSendToUser(operator, "/queue/reply", operatorMessageDto);
    }

    public void sendMessageToOperator(OperatorMessageDto operatorMessageDto, String operator) {
        simpMessagingTemplate.convertAndSendToUser(operator, "/queue/reply", operatorMessageDto);
    }

}
