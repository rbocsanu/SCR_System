package com.broker.social_companion_system.operator;

public enum OperatorMessageType {
    QUERY, // Confirm or deny client request queries
    MESSAGE, // Message from client or other operator
    LOG // Logging messages for debugging or notifications
}
