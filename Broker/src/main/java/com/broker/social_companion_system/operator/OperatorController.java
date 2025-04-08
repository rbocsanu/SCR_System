package com.broker.social_companion_system.operator;

import com.broker.social_companion_system.client.ClientEvent;
import com.broker.social_companion_system.client.ClientNotification;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.common_dtos.ResponseDto;
import com.broker.social_companion_system.global_services.QueueManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
@RequiredArgsConstructor
@MessageMapping("/operator")
public class OperatorController {

    private final OperatorManagementService operatorManagementService;
    private final QueueManager queueManager;

    @MessageMapping("/init_operator_connection")
    @SendToUser("/queue/responses")
    public ResponseDto<OperatorNotification> initOperatorConnection(@Payload boolean isAvailable) {
        String operatorUser = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.operatorConnected(operatorUser);

        if (isAvailable) {
            operatorManagementService.operatorMarkedAvailable(operatorUser);
        } else {
            operatorManagementService.operatorAnnouncedUnavailable(operatorUser);
        }

        return new ResponseDto<>(HttpStatus.OK, new OperatorNotification(OperatorEvent.ADD_ALL_UNITS,
                queueManager.getAvailableServers().toArray(new String[0])));
    }

    @MessageMapping("/mark_available")
    @SendToUser("/queue/responses")
    public ResponseDto<OperatorNotification> markAvailable(@Payload boolean isAvailable) {
        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        if (isAvailable) {
            operatorManagementService.operatorAnnouncedAvailable(operator);
        } else {
            operatorManagementService.operatorAnnouncedUnavailable(operator);
        }

        return new ResponseDto<>(HttpStatus.OK, new OperatorNotification(OperatorEvent.AVAILABLE, isAvailable));
    }

    /*
    @MessageMapping("/remove_operator_connection")
    @SendToUser("/queue/responses")
    public void removeOperatorConnection() {
        String operatorUser = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.operatorDisconnected(operatorUser);
    }

     */

    @MessageMapping("/respond_to_query_request")
    @SendToUser("/queue/responses")
    public void respondToQueryRequest(@RequestBody QueryPackage queryPackage) {
        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.respondToQueryRequest(queryPackage, operator);
    }

    @MessageMapping("/return_query_request")
    @SendToUser("/queue/responses")
    public void returnQueryRequest(@RequestBody QueryPackage queryPackage) {
        operatorManagementService.returnQueryRequest(queryPackage);
    }
}
