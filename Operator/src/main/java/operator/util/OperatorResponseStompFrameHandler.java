package operator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import operator.connection.OperatorManager;
import operator.entities.OperatorNotification;
import operator.dtos.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

@AllArgsConstructor
public class OperatorResponseStompFrameHandler implements StompFrameHandler {

    private OperatorManager operatorManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ResponseDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ObjectMapper mapper = new ObjectMapper();

        ResponseDto<?> responseDto = (ResponseDto) payload;

        assert (responseDto.statusCode() == HttpStatus.OK);

        OperatorNotification message = mapper.convertValue(responseDto.message(), OperatorNotification.class);

        System.out.println(message.getClass());
        System.out.println(message);

        operatorManager.handleResponse(message);
    }
}
