package com.workeando.plataform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Endpoint para el handshake STOMP/WebSocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // habilita SockJS para fallback
    }

    // Prefijos y broker simple en memoria
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para mensajes que procesará el servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Canales a los que los clientes se pueden suscribir
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefijo para colas privadas por usuario
        registry.setUserDestinationPrefix("/user");
    }
}
