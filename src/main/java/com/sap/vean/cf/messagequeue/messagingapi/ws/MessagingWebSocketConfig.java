package com.sap.vean.cf.messagequeue.messagingapi.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class MessagingWebSocketConfig implements WebSocketConfigurer {

	public static final String PATH_PREFIX = "/messaging/";
	public static final String PATH_SUFFIX = "/ws";

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), PATH_PREFIX + "*" + PATH_SUFFIX).setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MessagingWebSocket();
	}
}
