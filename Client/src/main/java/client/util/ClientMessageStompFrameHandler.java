package client.util;

import client.connection.ClientManager;
import client.dtos.ClientNotification;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

@AllArgsConstructor
public class ClientMessageStompFrameHandler implements StompFrameHandler {

    private ClientManager clientManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ClientNotification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Got msg " + payload);
        clientManager.handleResponse((ClientNotification) payload);
    }
}
