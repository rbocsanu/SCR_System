package com.broker.social_companion_system.operator;

import lombok.RequiredArgsConstructor;

public record OperatorMessageDto (
        Object messageContent,
        OperatorMessageType messageType
) {}
