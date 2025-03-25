package com.broker.social_companion_system.client;

import java.io.Serializable;

public record ClientNotification  (
        ClientEvent clientEvent,
        String[] message
) {
}

