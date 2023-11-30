import com.fasterxml.jackson.databind.JsonNode;
import com.hydra.angrygator.websocket.WebSocketConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AngrygatorApplication.class})
public class MessageBrokerTests {

    @Value("${local.server.port}")
    private int port;
    private String URL;
    private CompletableFuture<MeuDto> objCompletableFuture;

    @Autowired
    Service service;

    @BeforeEach
    void setup() {
        URL = "ws://localhost:" + port + "/ws";
    }

    @Test
    void teste1() throws Exception {
        // Instantiate client and subscribe channel
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        stompSession.subscribe(WebSocketConfig.CHANNEL1, new MeuStompFrameHandler());

        MeuDto dto1 = getDtoExample();
        service.sendNotificationToChannel1(dto);

        // getting results
        MeuDto dto2 = objCompletableFuture.get(10, SECONDS);
        assertEquals(dto1.toString(),dto2.toString());
    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class MeuStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return MeuDto.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            objCompletableFuture.complete((MeuDto) payload);
        }
        
    }

}
