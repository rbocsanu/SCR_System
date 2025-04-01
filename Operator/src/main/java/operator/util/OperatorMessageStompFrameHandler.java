package operator.util;

import lombok.AllArgsConstructor;
import operator.connection.OperatorManager;
import operator.dtos.OperatorNotification;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

@AllArgsConstructor
public class OperatorMessageStompFrameHandler implements StompFrameHandler {

    private OperatorManager operatorManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return OperatorNotification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Got msg " + payload);
        operatorManager.handleResponse((OperatorNotification) payload);
    }
}
