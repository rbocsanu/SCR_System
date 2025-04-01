package com.broker.social_companion_system.server;

import java.util.List;

public interface ServerDistributor {
    public String selectServer(List<String> availableServers);
}
