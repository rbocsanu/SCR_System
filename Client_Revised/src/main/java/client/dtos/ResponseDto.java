package client.dtos;

import org.springframework.http.HttpStatus;

public record ResponseDto<T> (
        HttpStatus statusCode,
        T message
) {

    public ResponseDto(HttpStatus code) {
        this(code, null);
    }
}
