package server.dtos;

public record ServerNotification(
        ServerNotificationType notificationType,
        Object message
        ) { }
