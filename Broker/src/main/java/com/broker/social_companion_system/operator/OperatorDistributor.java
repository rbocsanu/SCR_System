package com.broker.social_companion_system.operator;

import com.broker.social_companion_system.server.ServerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperatorDistributor implements Runnable {

    private final OperatorManagementService operatorManagementService;
    private final ServerManagementService serverManagementService;

    private int delayTime = 1000;
    private boolean running = true;


    @Override
    public void run() {

        while (running) {
            try {
                Thread.sleep(delayTime); 
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            try {
                operatorManagementService.distributeMaxPackagesToOperators();
            } catch (RuntimeException e) {
                log.error(e.getMessage());
            }

            try {
                serverManagementService.distributeMaxPackagesToServers();
            } catch (RuntimeException e) {
                log.error(e.getMessage());
            }

        }
    }

}
