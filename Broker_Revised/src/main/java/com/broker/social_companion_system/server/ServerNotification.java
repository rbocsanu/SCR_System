package com.broker.social_companion_system.server;

public record ServerNotification(
        ServerNotificationType notificationType,
        Object message
        ) {}
