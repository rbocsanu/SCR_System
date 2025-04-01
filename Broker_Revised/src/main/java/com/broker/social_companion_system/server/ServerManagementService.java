package com.broker.social_companion_system.server;

import com.broker.social_companion_system.client.ClientEvent;
import com.broker.social_companion_system.client.ClientNotification;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.global_services.QueueManager;
import com.broker.social_companion_system.global_services.ReplyMessageService;
import com.broker.social_companion_system.global_services.RequestType;
import com.broker.social_companion_system.global_services.VisualizeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerManagementService {

    private final ReplyMessageService replyMessageService;
    private final QueueManager queueManager;
    private final VisualizeService visualizeService;

    public void serverJoined(String server) {
        queueManager.addAvailableServer(server);
        replyMessageService.sendMessageToAllClients(new ClientNotification(ClientEvent.ADD_UNIT, new String[]{server}));
        visualizeService.publishVisual(RequestType.SERVER_CONNECTED, server);
    }

    public void serverDisconnect(String server) {
        queueManager.removeAvailableServer(server);
        replyMessageService.sendMessageToAllClients(new ClientNotification(ClientEvent.REMOVE_UNIT, new String[]{server}));
        visualizeService.publishVisual(RequestType.SERVER_DISCONNECTED, server);
    }

    public void serverCompletedQuery(QueryPackage queryPackage) {
        visualizeService.publishVisual(RequestType.QUERY_COMPLETED, queryPackage.query().getName());
    }

    public void distributeMaxPackagesToServers() {

        // TODO Remove print statements
        // System.out.println(queueManager.getServerPackageQueue());
        // System.out.println(queueManager.getAvailableServers());
        boolean nextPackageExists = true;

        while (nextPackageExists) {
            nextPackageExists = distributeNextPackageToServer();
        }
    }

    private boolean distributeNextPackageToServer() {
        //log.info("\nAvailable servers: " + availableServers +
        //        "\nServer package queue: " + Arrays.toString(serverPackageQueue.toArray()));
        if (queueManager.getAvailableServers().isEmpty() || queueManager.getServerPackageQueue().isEmpty()) {
            return false;
        }

        replyMessageService.sendPackageToServer(queueManager.getServerPackageQueue().poll());
        // TODO: default if no preferred unitID
        return true;
    }
}
