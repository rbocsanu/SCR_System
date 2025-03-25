package com.broker.social_companion_system.client;

import com.broker.social_companion_system.entities.Query;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

public interface QuerySelectorService {

    public Query select(String request);

}
