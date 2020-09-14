package net.mjg.mancalagame.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    
//    @Autowired
//    private SocketHandler socketHandler;
//    
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // todo CORS
//        registry.addHandler(socketHandler, "/echo").setAllowedOrigins("*");
//        registry.addHandler(socketHandler, "/game/*").setAllowedOrigins("*");
//    }
//}

