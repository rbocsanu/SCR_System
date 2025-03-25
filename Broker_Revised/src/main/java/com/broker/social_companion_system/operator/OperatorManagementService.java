package com.broker.social_companion_system.operator;

import com.broker.social_companion_system.client.ClientEvent;
import com.broker.social_companion_system.client.ClientNotification;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.global_services.QueueManager;
import com.broker.social_companion_system.global_services.ReplyMessageService;
import com.broker.social_companion_system.server.ServerManagementService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorManagementService {

    private final ReplyMessageService replyMessageService;
    private final ServerManagementService serverManagementService;
    private final QueueManager queueManager;

    @Getter
    private final LinkedList<String> availableOperators = new LinkedList<>();
    @Getter
    private final Set<String> busyOperators = new HashSet<>();

    public void operatorJoined(String operator) {
        operatorMarkedBusy(operator);
    }

    public void operatorRemoved(String operator) {
        availableOperators.remove(operator);
        busyOperators.remove(operator);
    }

    public void operatorAnnouncedAvailable(String operator) {
        busyOperators.remove(operator);
        operatorMarkedAvailable(operator);
    }

    public void operatorAnnouncedUnavailable(String operator) {
        availableOperators.remove(operator);
        operatorMarkedBusy(operator);
    }

    public void operatorMarkedBusy(String operator) {
        busyOperators.add(operator);
    }

    public void operatorMarkedAvailable(String operator) {
        availableOperators.addLast(operator);
    }

    public void respondToQueryRequest(QueryPackage queryPackage, String operator /*, boolean available*/) {
        if (queryPackage.approved()) {
            queueManager.addPackageToServerQueue(queryPackage);
            replyMessageService.sendMessageToClient(
                    new ClientNotification(ClientEvent.ADD_SCHEDULED, new String[]{queryPackage.query().getName()}),
                    queryPackage.requestingUser()
            );
        }

        replyMessageService.sendMessageToClient(
                new ClientNotification(ClientEvent.REMOVE_PENDING, new String[]{queryPackage.query().getName()}),
                queryPackage.requestingUser()
        );

        /*
        if (available) {
            operatorMarkedAvailable(operator);
        }
        */
    }

    public void distributeMaxPackagesToOperators() {
        boolean nextPackageExists = true;

        while (nextPackageExists) {
            nextPackageExists = distributeNextPackageToOperator();
        }
    }

    public boolean distributeNextPackageToOperator() {
        if (availableOperators.isEmpty() || queueManager.getOperatorPackageQueue().isEmpty()) {
            return false;
        }

        String operator = availableOperators.poll();
        operatorMarkedBusy(operator);

        //OperatorMessageDto operatorMessageDto = new OperatorMessageDto(packageQueue.poll(), OperatorMessageType.QUERY);
        //log.info("Package queue: " + packageQueue + "\nIs empty: ");
        replyMessageService.sendMessageToOperator(queueManager.getOperatorPackageQueue().poll(), operator);

        return true;
    }

}
