package server.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import server.dtos.QueryPackage;
import server.dtos.ServerGuiEvent;
import server.dtos.ServerNotification;
import server.entities.Query;
import server.unit.ObservableUnit;
import server.unit.RobotUnit;
import server.userinterface.ServerObserver;
import server.util.ServerReplyFrameHandler;
import server.util.ServerSessionHandler;
import server.util.ServerResponseFrameHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ServerManager implements ServerObserver {

    private final int port = 8080;
    private final String wsUrl = "ws://localhost:" + port + "/broker";

    private final RobotUnit managingUnit;
    private final ObservableUnit observableUnit;

    //private final HashMap<Query, String> queryClientIdMap = new HashMap<>();
    private ServerSessionHandler sessionHandler;
    private ServerResponseFrameHandler responseFrameHandler;
    private ServerReplyFrameHandler replyFrameHandler;
    private StompSession session;

    public void initConnection(String authUser, String authPass) {

        observableUnit.register(this);

        try {
            sessionHandler = new ServerSessionHandler();
            session = startSession(authUser, authPass, sessionHandler);

            responseFrameHandler = new ServerResponseFrameHandler(this);
            replyFrameHandler = new ServerReplyFrameHandler(this);

            session.subscribe("/user/queue/responses", responseFrameHandler);
            session.subscribe("/user/queue/reply", replyFrameHandler);
            session.subscribe("/public/server", replyFrameHandler);

            session.send("/app/server/init_server_connection", "");

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleResponse(ServerNotification notification) {

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Notification type: " + notification.notificationType());

        switch (notification.notificationType()) {
            case QUERY -> {
                QueryPackage queryPackage = mapper.convertValue(notification.message(), QueryPackage.class);

                //queryClientIdMap.put(queryPackage.query(), queryPackage.requestingUser());
                managingUnit.addActivityToQueue(queryPackage);
                break;
            }
        }
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

    public void update(ServerGuiEvent guiEvent, Object msg) {


        try {
            switch (guiEvent) {
                case EXECUTE_START:
                    if (!(msg instanceof QueryPackage queryPackage)) {
                        return;
                    }
                    //String clientId = queryClientIdMap.remove(msgActivity);

                    session.send("/app/server/started_query", queryPackage);
                    break;

                case EXECUTE_COMPLETE:
                    if (!(msg instanceof QueryPackage queryPackage)) {
                        return;
                    }

                    session.send("/app/server/completed_query", queryPackage);
                    break;

                default:

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
