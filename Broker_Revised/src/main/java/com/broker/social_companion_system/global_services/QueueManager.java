package com.broker.social_companion_system.global_services;

import com.broker.social_companion_system.common_dtos.QueryPackage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

@Getter
@Service
@RequiredArgsConstructor
public class QueueManager {

    private final PriorityQueue<QueryPackage> operatorPackageQueue = new PriorityQueue<>();
    private final PriorityQueue<QueryPackage> serverPackageQueue = new PriorityQueue<>();
    private final Set<String> availableServers = new HashSet<>();


    public void addPackageToOperatorQueue(QueryPackage queryPackage) {
        operatorPackageQueue.add(queryPackage);
    }

    public void addPackageToServerQueue(QueryPackage queryPackage) {
        serverPackageQueue.add(queryPackage);
    }

    public void addAvailableServer(String server) {
        availableServers.add(server);
    }

    public void removeAvailableServer(String server) {
        availableServers.remove(server);
    }
}
