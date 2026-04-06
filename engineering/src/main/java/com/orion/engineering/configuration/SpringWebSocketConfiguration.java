package com.orion.engineering.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SpringWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//registry.addEndpoint("/websocket").setAllowedOrigins("http://localhost:8081");
		registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
		//registry.addEndpoint("/websocket").setAllowedOriginPatterns(Orion.domainNameFrontend).withSockJS();
	}


	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setCacheLimit(0);
		registry.setPreservePublishOrder(true);
		registry.setApplicationDestinationPrefixes("/app");
	}
}
