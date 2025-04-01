package com.broker.social_companion_system;

import com.broker.social_companion_system.client.*;
import com.broker.social_companion_system.common_dtos.QueryPackage;
import com.broker.social_companion_system.common_dtos.ResponseDto;
import com.broker.social_companion_system.entities.ServiceQuery;
import com.broker.social_companion_system.entities.TaskQuery;
import com.broker.social_companion_system.operator.OperatorManagementService;
import com.broker.social_companion_system.operator.OperatorMessageDto;
import com.broker.social_companion_system.operator.OperatorMessageType;
import com.broker.social_companion_system.server.ServerNotification;
import com.broker.social_companion_system.server.ServerNotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BrokerApplication.class)
public class BrokerTests {

    @MockitoBean
    private QuerySelectorService querySelectorService;

    @InjectMocks
    private ClientService clientService;

    @Autowired
    private OperatorManagementService operatorManagementService;

    @Value("${local.server.port}")
    private int port;
    private String wsUrl;
    private String httpUrl;

    @Before
    public void setup() {
        wsUrl = "ws://localhost:" + port + "/broker";
        httpUrl = "http://localhost:" + port + "/";
    }

    @Test
    public void clientSendMessageWorks() throws ExecutionException, InterruptedException, TimeoutException, JSONException {
        when(querySelectorService.select("Hey")).thenReturn(
                new ServiceQuery("Hey", 5, 500)
        );

        StompSession session = startSession("client1", "password1");

        List<CompletableFuture<String>> publicFutures = List.of(new CompletableFuture<>());
        session.subscribe("/topic/public", TestStompFrameHandler.of(String.class, publicFutures));

        /*
        JSONObject query = new JSONObject();
        query.put("message", "Hello from Client!");
        query.put("requestedUnitId", "5432");

        */

        QueryDto query = new QueryDto("Hey", "5432");
        session.send("/app/client/request_query", query);

        //String result = completableFuture.get(5, TimeUnit.SECONDS);
        String result = publicFutures.get(0).get(5, TimeUnit.SECONDS);

        log.info("Returned Result: " + result);
        Assert.assertNotNull(result);
    }

    @Test
    public void operatorConnects() throws ExecutionException, InterruptedException, TimeoutException {
        StompSession operatorSession = startSession("operator1", "opassword1");

        List<CompletableFuture<OperatorMessageDto>> operatorFutures = List.of(new CompletableFuture<>());
        operatorSession.subscribe("/topic/public",
                TestStompFrameHandler.of(OperatorMessageDto.class, operatorFutures)
        );

        operatorSession.send("/app/operator/init_operator_connection", true);

        OperatorMessageDto confirmationLog = operatorFutures.get(0).get(5, TimeUnit.SECONDS);

        Set<String> busyOperators = operatorManagementService.getBusyOperators();
        log.info(busyOperators.toString());

        assert(confirmationLog.messageType() == OperatorMessageType.LOG);
        assert(confirmationLog.messageContent().equals("Connected"));
        assert(busyOperators.contains("operator1"));
    }

    @Test
    public void operatorMarksAvailable() throws ExecutionException, InterruptedException, TimeoutException {
        StompSession operatorSession = startSession("operator1", "opassword1");

        List<CompletableFuture<OperatorMessageDto>> operatorFutures = List.of(
                new CompletableFuture<>(),
                new CompletableFuture<>());
        operatorSession.subscribe("/topic/public",
                TestStompFrameHandler.of(OperatorMessageDto.class, operatorFutures)
        );

        operatorSession.send("/app/operator/init_operator_connection", true);
        operatorSession.send("/app/operator/mark_available", true);

        OperatorMessageDto connectionLog = operatorFutures.get(0).get(5, TimeUnit.SECONDS);
        OperatorMessageDto availabilityLog = operatorFutures.get(1).get(5, TimeUnit.SECONDS);

    }


    @Test
    public void singleOperatorReceivesTask() throws ExecutionException, InterruptedException, TimeoutException {
        when(querySelectorService.select("Testing Request...")).thenReturn(
                new TaskQuery("Testing Request...", 5, 500)
        );

        StompSession clientSession = startSession("client1", "password1");
        StompSession operatorSession = startSession("operator1", "opassword1");

        List<CompletableFuture<OperatorMessageDto>> operatorFutures = List.of(
                new CompletableFuture<>()
        );
        operatorSession.subscribe("/user/queue/responses",
                TestStompFrameHandler.of(OperatorMessageDto.class, operatorFutures)
        );

        operatorSession.send("/app/operator/init_operator_connection", true);
        OperatorMessageDto connectionLog = operatorFutures.get(0).get(5, TimeUnit.SECONDS);
        log.info("Connection log: " + connectionLog.messageContent());
        assert (connectionLog.messageContent().equals("Connected"));

        List<CompletableFuture<QueryPackage>> operatorQueryFutures = List.of(
                new CompletableFuture<>()
        );

        operatorSession.subscribe("/user/queue/reply",
                TestStompFrameHandler.of(QueryPackage.class, operatorQueryFutures)
        );

        List<CompletableFuture<ResponseDto>> clientFutures = List.of(
                new CompletableFuture<>()
        );
        clientSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, clientFutures)
        );

        QueryDto query = new QueryDto("Testing Request...", "5432");
        clientSession.send("/app/client/request_query", query);

        ResponseDto<String> clientQueryResponse = clientFutures.get(0).get(5, TimeUnit.SECONDS);
        QueryPackage operatorQueryReceived = operatorQueryFutures.get(0).get(5, TimeUnit.SECONDS);

        assert (clientQueryResponse.statusCode().value() == 200);

        log.info(operatorQueryReceived.query().getName());
        assert (operatorQueryReceived.query().getName().equals("Testing Request..."));

    }

    @Test
    public void singleRobotReceivesService() throws ExecutionException, InterruptedException, TimeoutException {
        when(querySelectorService.select("Testing Request...")).thenReturn(
                new ServiceQuery("Testing Request...", 5, 500)
        );

        StompSession serverSession = startSession("1111", "rpassword1");
        List<CompletableFuture<ResponseDto>> serverResponseFutures = List.of(new CompletableFuture<>());
        List<CompletableFuture<ServerNotification>> serverReplyFutures = List.of(new CompletableFuture<>());
        serverSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, serverResponseFutures)
        );
        serverSession.subscribe(
                "/user/queue/reply",
                TestStompFrameHandler.of(ServerNotification.class, serverReplyFutures)
        );
        serverSession.send("/app/server/init_server_connection", true);

        StompSession clientSession = startSession("client1", "password1");
        List<CompletableFuture<ResponseDto>> clientFutures = List.of(
                new CompletableFuture<>()
        );
        clientSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, clientFutures)
        );
        QueryDto query = new QueryDto("Testing Request...", "1111");
        clientSession.send("/app/client/request_query", query);

        ServerNotification serverNotification = serverReplyFutures.get(0).get(5, TimeUnit.SECONDS);
        assert (serverNotification.notificationType() == ServerNotificationType.QUERY);

        ObjectMapper mapper = new ObjectMapper();

        QueryPackage receivedServerQuery = mapper.convertValue(serverNotification.message(), QueryPackage.class);
        log.info("Received server query " + receivedServerQuery.query().getName());

        assert (receivedServerQuery.query().getName().equals("Testing Request..."));
    }

    @Test
    public void singleRobotReceivesApprovedTask() throws ExecutionException, InterruptedException, TimeoutException {
        when(querySelectorService.select("Testing Request...")).thenReturn(
                new TaskQuery("Testing Request...", 5, 500)
        );

        StompSession operatorSession = startSession("operator1", "opassword1");
        List<CompletableFuture<OperatorMessageDto>> operatorResponseFutures = List.of(
                new CompletableFuture<>(),
                new CompletableFuture<>());
        operatorSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(OperatorMessageDto.class, operatorResponseFutures)
        );
        List<CompletableFuture<QueryPackage>> operatorReplyFutures = List.of(new CompletableFuture<>());
        operatorSession.subscribe("/user/queue/reply",
                TestStompFrameHandler.of(QueryPackage.class, operatorReplyFutures)
        );
        operatorSession.send("/app/operator/init_operator_connection", true);

        StompSession serverSession = startSession("1111", "rpassword1");
        List<CompletableFuture<ResponseDto>> serverResponseFutures = List.of(new CompletableFuture<>());
        List<CompletableFuture<ServerNotification>> serverReplyFutures = List.of(new CompletableFuture<>());
        serverSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, serverResponseFutures)
        );
        serverSession.subscribe(
                "/user/queue/reply",
                TestStompFrameHandler.of(ServerNotification.class, serverReplyFutures)
        );
        serverSession.send("/app/server/init_server_connection", true);

        OperatorMessageDto connectionLog = operatorResponseFutures.get(0).get(5, TimeUnit.SECONDS);
        log.info("Connection log: " + connectionLog.messageContent());

        StompSession clientSession = startSession("client1", "password1");
        List<CompletableFuture<ResponseDto>> clientFutures = List.of(
                new CompletableFuture<>()
        );
        clientSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, clientFutures)
        );
        QueryDto query = new QueryDto("Testing Request...", "1111");
        clientSession.send("/app/client/request_query", query);

        QueryPackage receivedOperatorQuery = operatorReplyFutures.get(0).get(5, TimeUnit.SECONDS);
        operatorSession.send("/app/operator/respond_to_query_request",
                new QueryPackage(
                        receivedOperatorQuery.query(),
                        receivedOperatorQuery.requestedUnitId(),
                        receivedOperatorQuery.requestingUser(),
                        true
                        )
        );

        ServerNotification serverNotification = serverReplyFutures.get(0).get(5, TimeUnit.SECONDS);
        assert (serverNotification.notificationType() == ServerNotificationType.QUERY);
        // assert (serverNotification.message().getClass() == QueryPackage.class);

        ObjectMapper mapper = new ObjectMapper();

        QueryPackage receivedServerQuery = mapper.convertValue(serverNotification.message(), QueryPackage.class);
        log.info("Received server query " + receivedServerQuery.query().getName());

        assert (receivedServerQuery.query().getName().equals("Testing Request..."));

    }

    @Test
    public void clientNotifiedOfNewServer() throws ExecutionException, InterruptedException, TimeoutException {

        StompSession clientSession = startSession("client1", "password1");
        List<CompletableFuture<ResponseDto>> clientResponseFutures = List.of(
                new CompletableFuture<>()
        );
        List<CompletableFuture<ClientNotification>> clientPublicFutures = List.of(
                new CompletableFuture<>()
        );
        clientSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, clientResponseFutures)
        );
        clientSession.subscribe(
                "/public/client",
                TestStompFrameHandler.of(ClientNotification.class, clientPublicFutures)
        );

        clientSession.send("/app/client/init_client_connection", "");
        ResponseDto<?> initResponse = clientResponseFutures.get(0).get(5, TimeUnit.SECONDS);
        log.info("Status code: " + initResponse.statusCode());

        StompSession serverSession = startSession("1111", "rpassword1");
        List<CompletableFuture<ResponseDto>> serverResponseFutures = List.of(new CompletableFuture<>());
        serverSession.subscribe(
                "/user/queue/responses",
                TestStompFrameHandler.of(ResponseDto.class, serverResponseFutures)
        );
        serverSession.send("/app/server/init_server_connection", true);

        ClientNotification notification = clientPublicFutures.get(0).get(5, TimeUnit.SECONDS);
        log.info("Client notification: " + notification);

        assert (notification.clientEvent() == ClientEvent.ADD_UNIT);
        assert (notification.message()[0].equals("1111"));

    }

    public StompSession startSession(String authUser, String authPass) throws ExecutionException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        List<MessageConverter> converters = new ArrayList<>();
        converters.add(new MappingJackson2MessageConverter());
        converters.add(new StringMessageConverter());
        stompClient.setMessageConverter(new CompositeMessageConverter(converters));

        StompSessionHandler sessionHandler = new TestingSessionHandler();

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        StompHeaders headers = new StompHeaders();
        headers.add("username", authUser);
        headers.add("password", authPass);

        return stompClient.connectAsync(wsUrl, handshakeHeaders, headers, sessionHandler).get();

    }

    private static class TestingSessionHandler extends StompSessionHandlerAdapter {
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

    @RequiredArgsConstructor
    private static class TestStompFrameHandler<T> implements StompFrameHandler {

        private final Class<T> payloadType;

        private final List<CompletableFuture<T>> completableFutures;
        private int index = 0;

        public static <T> TestStompFrameHandler<T> of(Class<T> payloadType, List<CompletableFuture<T>> completableFutures) {
            return (new TestStompFrameHandler<>(payloadType, completableFutures));
        }

        @Override
        @NonNull
        public Type getPayloadType(@NonNull StompHeaders headers) {
            return payloadType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleFrame(@NonNull StompHeaders headers, @NonNull Object payload) {
            log.info("Incoming frame class: " + payload.getClass());
            completableFutures.get(index).complete((T) payload);
            index++;
        }
    }

}
