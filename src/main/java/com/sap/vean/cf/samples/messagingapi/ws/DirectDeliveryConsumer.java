package com.sap.vean.cf.samples.messagingapi.ws;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class DirectDeliveryConsumer extends DefaultConsumer {

	private WebSocketSession session;

	private DirectDeliveryConsumer(Channel channel) {
		super(channel);
	}
	
	public DirectDeliveryConsumer(Channel channel, WebSocketSession session){
		super(channel);
		this.session = session;
	}
	
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
			byte[] body) throws IOException {
		TextMessage message = new TextMessage(body);
		session.sendMessage(message);
	}

}
