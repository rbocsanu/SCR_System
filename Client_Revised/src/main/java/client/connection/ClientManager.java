package client.connection;

import client.dtos.ClientEvent;
import client.dtos.ClientNotification;
import client.dtos.QueryDto;
import client.util.ClientMessageStompFrameHandler;
import client.util.ClientSessionHandler;
import client.util.ClientStompFrameHandler;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ClientManager extends ObservableClient {

    private final int port = 8080;
    private final String wsUrl = "ws://localhost:" + port + "/broker";

    private ClientSessionHandler sessionHandler;
    private ClientStompFrameHandler stompFrameHandler;
    private ClientMessageStompFrameHandler messageStompFrameHandler;
    private StompSession session;

    public void initConnection(String authUser, String authPass) {

        try {
            sessionHandler = new ClientSessionHandler();
            session = startSession(authUser, authPass, sessionHandler);

            session.send("/app/client/init_client_connection", "");

            stompFrameHandler = new ClientStompFrameHandler(this);
            messageStompFrameHandler = new ClientMessageStompFrameHandler(this);

            session.subscribe("/user/queue/responses", stompFrameHandler);
            session.subscribe("/user/queue/reply", messageStompFrameHandler);
            //session.subscribe("/topic/public", stompFrameHandler);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void sendRequest(String requestMsg, String preferredUnitID) {
        System.out.println(requestMsg + " " + preferredUnitID);

        QueryDto queryDto = new QueryDto(requestMsg, preferredUnitID);
        session.send("/app/client/request_query", queryDto);

        notifyAll(ClientEvent.SET_CURRENT_INPUT, new String[]{""});
    }

    public void handleResponse(ClientNotification notification) {
        notifyAll(notification.clientEvent(), notification.message());
    }

    private StompSession startSession(String authUser, String authPass, StompSessionHandler sessionHandler)
            throws ExecutionException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        List<MessageConverter> converters = new ArrayList<>();
        converters.add(new MappingJackson2MessageConverter()); // used to handle json messages
        converters.add(new StringMessageConverter()); // used to handle raw strings
        stompClient.setMessageConverter(new CompositeMessageConverter(converters));

        //StompSessionHandler sessionHandler = new TestingSessionHandler();

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

    /*
    public void addAllUnits(ArrayList<String> units) {
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (String unitId : units) {
            unitConnected(unitId);
        }
    }

    public void unitConnected(String id) {
        notifyAll(ClientGuiEvent.ADD_UNIT, new String[]{id});
    }

    public void unitDisconnected(String id) {
        notifyAll(ClientGuiEvent.REMOVE_UNIT, new String[]{id});
    }

    public void pendingAdded(String requestName) {
        notifyAll(ClientGuiEvent.ADD_PENDING, new String[]{requestName});
    }

    public void pendingRemoved(String requestName) {
        notifyAll(ClientGuiEvent.REMOVE_PENDING, new String[]{requestName});
    }

    public void scheduledAdded(String requestName) {
        notifyAll(ClientGuiEvent.ADD_SCHEDULED, new String[]{requestName});
    }

    public void scheduledRemoved(String requestName) {
        notifyAll(ClientGuiEvent.REMOVE_SCHEDULED, new String[]{requestName});
    }

     */

}
