package server.util;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import server.connection.ServerManager;
import server.dtos.ResponseDto;
import server.dtos.ServerNotification;

import java.lang.reflect.Type;

@AllArgsConstructor
public class ServerStompFrameHandler implements StompFrameHandler {

    private ServerManager serverManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ResponseDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ResponseDto<?> responseDto = (ResponseDto<?>) payload;

        assert (responseDto.statusCode() == HttpStatus.OK);
        assert (responseDto.message().getClass() == ServerNotification.class);

        System.out.println(responseDto);

        serverManager.handleResponse((ServerNotification) responseDto.message());
    }
}
