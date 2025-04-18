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
    @Getter
    private final Set<String> unavailableOperators = new HashSet<>();

    public void operatorConnected(String operator) {
        operatorMarkedBusy(operator);
    }

    public void operatorDisconnected(String operator) {
        availableOperators.remove(operator);
        busyOperators.remove(operator);
    }

    public void operatorAnnouncedAvailable(String operator) {
        unavailableOperators.remove(operator);
        operatorMarkedAvailable(operator);
    }

    public void operatorAnnouncedUnavailable(String operator) {
        availableOperators.remove(operator);
        busyOperators.remove(operator);
        unavailableOperators.add(operator);
    }

    public void operatorMarkedBusy(String operator) {
        if (unavailableOperators.contains(operator)) return;
        busyOperators.add(operator);
    }

    public void operatorMarkedAvailable(String operator) {
        if (unavailableOperators.contains(operator)) return;
        availableOperators.addLast(operator);
    }

    public void respondToQueryRequest(QueryPackage queryPackage, String operator) {
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

        operatorMarkedAvailable(operator);
    }

    public void returnQueryRequest(QueryPackage queryPackage) {
        queueManager.addPackageToOperatorQueue(queryPackage);
    }

    public void distributeMaxPackagesToOperators() {
        boolean nextPackageExists = true;

        while (nextPackageExists) {
            nextPackageExists = distributeNextPackageToOperator();
        }
    }

    public boolean distributeNextPackageToOperator() {
        log.info("Available Operators: " + availableOperators);
        log.info("Package queue: " + queueManager.getOperatorPackageQueue());
        if (availableOperators.isEmpty() || queueManager.getOperatorPackageQueue().isEmpty()) {
            return false;
        }

        String operator = availableOperators.poll();
        operatorMarkedBusy(operator);

        log.info("Distributing package: " + queueManager.getOperatorPackageQueue().peek() + " to: " + operator);

        //OperatorMessageDto operatorMessageDto = new OperatorMessageDto(packageQueue.poll(), OperatorMessageType.QUERY);
        //log.info("Package queue: " + packageQueue + "\nIs empty: ");

        OperatorNotification operatorNotification = new OperatorNotification(OperatorEvent.NEW_TASK, queueManager.getOperatorPackageQueue().poll());

        replyMessageService.sendMessageToOperator(operatorNotification, operator);

        return true;
    }

}
