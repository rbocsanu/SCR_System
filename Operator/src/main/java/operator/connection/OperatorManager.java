package operator.connection;

import lombok.Getter;
import operator.dtos.QueryPackage;
import operator.entities.OperatorEvent;
import operator.entities.OperatorNotification;
import operator.util.OperatorMessageStompFrameHandler;
import operator.util.OperatorSessionHandler;
import operator.util.OperatorResponseStompFrameHandler;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OperatorManager extends ObservableOperator {

    private final int port = 8080;
    private final String wsUrl = "ws://localhost:" + port + "/broker";

    private OperatorSessionHandler sessionHandler;
    private OperatorResponseStompFrameHandler stompFrameHandler;
    private OperatorMessageStompFrameHandler messageStompFrameHandler;
    private StompSession session;

    public void initConnection(String authUser, String authPass) {

        try {
            sessionHandler = new OperatorSessionHandler();
            session = startSession(authUser, authPass, sessionHandler);

            session.send("/app/operator/init_operator_connection", true);

            stompFrameHandler = new OperatorResponseStompFrameHandler(this);
            messageStompFrameHandler = new OperatorMessageStompFrameHandler(this);

            session.subscribe("/user/queue/responses", stompFrameHandler);
            session.subscribe("/user/queue/reply", messageStompFrameHandler);
            session.subscribe("/public/operator", messageStompFrameHandler);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void decideOnTask(QueryPackage queryPackage) {
        session.send("/app/operator/respond_to_query_request", queryPackage);
    }

    public void announceAvailability(boolean available, QueryPackage currentlySelectingQueryPackage) {
        session.send("/app/operator/mark_available", available);
        if (!available && currentlySelectingQueryPackage != null) {
            session.send("/app/operator/return_query_request", currentlySelectingQueryPackage);
        }
    }

    public void handleResponse(OperatorNotification notification) {
        System.out.println("Handling response: " + notification);
        notifyAll(notification.operatorEvent(), notification.message());
    }

    private StompSession startSession(String authUser, String authPass, StompSessionHandler sessionHandler)
            throws ExecutionException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        List<MessageConverter> converters = new ArrayList<>();
        converters.add(new MappingJackson2MessageConverter());
        converters.add(new StringMessageConverter());
        stompClient.setMessageConverter(new CompositeMessageConverter(converters));

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        StompHeaders headers = new StompHeaders();
        headers.add("username", authUser);
        headers.add("password", authPass);

        return stompClient.connectAsync(wsUrl, handshakeHeaders, headers, sessionHandler).get();
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }
}
