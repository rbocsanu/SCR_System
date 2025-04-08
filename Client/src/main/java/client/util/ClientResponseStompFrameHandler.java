package client.util;

import client.connection.ClientManager;
import client.dtos.ClientNotification;
import client.dtos.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

@AllArgsConstructor
public class ClientResponseStompFrameHandler implements StompFrameHandler {

    private ClientManager clientManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ResponseDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ObjectMapper mapper = new ObjectMapper();

        ResponseDto<?> responseDto = (ResponseDto) payload;

        assert (responseDto.statusCode() == HttpStatus.OK);

        ClientNotification message = mapper.convertValue(responseDto.message(), ClientNotification.class);

        System.out.println(message.getClass());
        System.out.println(message);

        clientManager.handleResponse(message);
    }
}
