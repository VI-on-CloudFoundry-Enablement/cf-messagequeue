package com.sap.vean.cf.samples.messagingapi.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class MessagingWebSocketConfig implements WebSocketConfigurer{

	 public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {		
		 registry.addHandler(myHandler(), "/messaging/*/ws").setAllowedOrigins("*");
	}

	 @Bean
	    public WebSocketHandler myHandler() {
	        return new MessagingWebSocket();
	    }
}
