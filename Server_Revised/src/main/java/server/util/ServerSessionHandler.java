package server.util;

import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ServerSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
        System.out.println("Connected to server");
    }

    @Override
    public void handleException(@NonNull StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(@NonNull StompSession session, Throwable exception) {
        exception.printStackTrace();
    }
}
