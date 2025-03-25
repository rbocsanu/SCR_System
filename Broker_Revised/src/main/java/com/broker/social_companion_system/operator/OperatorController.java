package com.broker.social_companion_system.operator;

import com.broker.social_companion_system.common_dtos.QueryPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @MessageMapping("/init_operator_connection")
    @SendToUser("/queue/responses")
    public OperatorMessageDto initOperatorConnection(@Payload boolean isAvailable) {
        String operatorUser = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.operatorJoined(operatorUser);

        if (isAvailable) {
            operatorManagementService.operatorMarkedAvailable(operatorUser);
        }

        return new OperatorMessageDto("Connected", OperatorMessageType.LOG);
    }

    @MessageMapping("/mark_available")
    @SendToUser("/queue/responses")
    public OperatorMessageDto markAvailable(@Payload boolean isAvailable) {
        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        if (isAvailable) {
            operatorManagementService.operatorMarkedAvailable(operator);
        } else {
            operatorManagementService.operatorMarkedBusy(operator);
        }

        return new OperatorMessageDto(
                "Marked: " + (isAvailable ? "available" : "busy"),
                OperatorMessageType.LOG
        );
    }

    @MessageMapping("/remove_operator_connection")
    @SendTo("/topic/public")
    public void removeOperatorConnection() {
        String operatorUser = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.operatorRemoved(operatorUser);
    }

    @MessageMapping("/respond_to_query_request")
    @SendTo("/topic/public")
    public void respondToQueryRequest(@RequestBody QueryPackage queryPackage) {
        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        operatorManagementService.respondToQueryRequest(queryPackage, operator);
    }

}
