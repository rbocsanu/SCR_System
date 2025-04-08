package com.broker.social_companion_system.operator;

public record OperatorNotification(
        OperatorEvent operatorEvent,
        Object message
) {}
