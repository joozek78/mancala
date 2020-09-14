package net.mjg.mancalagame.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mjg.mancalagame.controller.InvalidMessageException;
import net.mjg.mancalagame.controller.WebSocketController;
import net.mjg.mancalagame.controller.WsConnection;
import net.mjg.mancalagame.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;

@Component
public class SocketHandler extends TextWebSocketHandler {
    Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final ObjectMapper objectMapper;
    private final WebSocketController controller;

    public SocketHandler(ObjectMapper objectMapper, WebSocketController controller){
        this.objectMapper = objectMapper;
        this.controller = controller;
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
            throws IOException {
        logger.info("Message received: {}", message.getPayload());

        try {
            MessageContainer messageContainer = objectMapper.readValue(message.getPayload(), MessageContainer.class);
            var gameMessage = messageContainer.unwrap(objectMapper);
            var gameId = extractGameId(session);
            ConnectionWrapper connection = new ConnectionWrapper(session);
            
            dispatchMessage(gameMessage, gameId, connection, messageContainer);
        } catch (JsonProcessingException | InvalidMessageException e) {
            logger.error(String.format("Could not process message '%s' on connection %s", message, session), e);
            ErrorMessage errorMessage = new ErrorMessage(String.format("Could not format message '%s'", message));
            sendWrappedMessage(errorMessage, session);
        }
    }

    private void dispatchMessage( Message gameMessage, 
                                  String gameId,
                                  ConnectionWrapper connection,
                                  MessageContainer messageContainer) throws IOException, InvalidMessageException {
        if(gameMessage instanceof NewGameMessage msg) {
            controller.handleNewGame(msg, gameId, connection);
        } else if (gameMessage instanceof JoinGameMessage msg) {
            controller.handleJoin(msg, gameId, connection);
        } else if (gameMessage instanceof MoveMessage msg) {
            controller.handleMove(msg, gameId, connection);
        } else {
            throw new InvalidMessageException("Unexpected message type: " + messageContainer.getType());
        }
    }

    private String extractGameId(WebSocketSession session) throws InvalidMessageException {
        URI uri = session.getUri();
        if(uri == null) {
            throw new InvalidMessageException("URI missing in the session");
        }
        String[] segments = uri.getPath().split("/");
        return segments[segments.length - 1];
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        logger.info("Received connection: {}", session);
    }
    
    public void sendWrappedMessage(Message message, WebSocketSession session) throws IOException {
        MessageContainer messageContainer = MessageContainer.wrap(message, objectMapper);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageContainer)));
    }
    
    private class ConnectionWrapper implements WsConnection {
        private final WebSocketSession session;

        public ConnectionWrapper(WebSocketSession session) {
            this.session = session;
        }

        @Override
        public void sendMessage(Message message) throws IOException {
            sendWrappedMessage(message, session);
        }
    }
}

