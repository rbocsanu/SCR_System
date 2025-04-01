package server.util;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import server.connection.ServerManager;
import server.dtos.ServerNotification;

import java.lang.reflect.Type;

@AllArgsConstructor
public class ServerReplyFrameHandler implements StompFrameHandler {

    private ServerManager serverManager;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ServerNotification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Got frame: " + payload);
        serverManager.handleResponse((ServerNotification) payload);
    }
}
