package com.broker.social_companion_system.client;

import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.entities.Query;
import com.broker.social_companion_system.entities.ServiceQuery;
import com.broker.social_companion_system.global_services.QueueManager;
import com.broker.social_companion_system.operator.OperatorManagementService;
import com.broker.social_companion_system.global_services.ReplyMessageService;
import com.broker.social_companion_system.server.ServerManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ClientQueryService {

    public final QuerySelectorService selector;
    public final ReplyMessageService replyMessageService;
    private final QueueManager queueManager;

    public boolean handleQueryRequest(String message, String requestedUnitId, String requestingUser) {
        Query requestedQuery = selector.select(message);

        boolean approved = requestedQuery.getClass() == ServiceQuery.class;
        QueryPackage queryPackage = new QueryPackage(requestedQuery, requestedUnitId, requestingUser, approved);

        log.info("Query package sent by client: " + queryPackage.query() + " is approved?: " + queryPackage.approved());

        if (approved) {
            queueManager.addPackageToServerQueue(queryPackage);
        } else {
            queueManager.addPackageToOperatorQueue(queryPackage);
        }

        return approved;
    }

}
