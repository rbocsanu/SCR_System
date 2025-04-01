package com.broker.social_companion_system.common_dtos;

import org.springframework.http.HttpStatus;

public record ResponseDto<T> (
        HttpStatus statusCode,
        T message
) {

    public ResponseDto(HttpStatus code) {
        this(code, null);
    }
}
