package com.sap.vean.cf.messagequeue.messagingapi.ws;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sap.vean.cf.messagequeue.messagingapi.MessagingAPIService;

public class MessagingWebSocket extends TextWebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(MessagingWebSocket.class);

	private Connection conn;
	private Channel chl;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		String uri = session.getUri().toString();		
		String queue = uri.substring(uri.lastIndexOf(MessagingWebSocketConfig.PATH_PREFIX) + MessagingWebSocketConfig.PATH_PREFIX.length());
		queue = queue.substring(0, queue.lastIndexOf(MessagingWebSocketConfig.PATH_SUFFIX));
		
		String fullQueueName = MessagingAPIService.QUEUE_PREFIX + queue;
				
		try {
		
			//Create and Publish Message
			byte[] messageBodyBytes = message.getPayload().getBytes("UTF-8");
			chl.basicPublish("", fullQueueName, null, messageBodyBytes);
	
			TextMessage ts = new TextMessage(("msg received and put on " + fullQueueName).getBytes());
			session.sendMessage(ts);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			TextMessage ts = new TextMessage(e.toString().getBytes());
			try {
				session.sendMessage(ts);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);

		
		try {

			String uri = session.getUri().toString();		
			String queue = uri.substring(uri.lastIndexOf(MessagingWebSocketConfig.PATH_PREFIX) + MessagingWebSocketConfig.PATH_PREFIX.length());
			queue = queue.substring(0, queue.lastIndexOf(MessagingWebSocketConfig.PATH_SUFFIX));
			
			String fullQueueName = MessagingAPIService.QUEUE_PREFIX + queue;
			conn = getConnectionFactory().newConnection();

			// Create channel and declare queue
			chl = conn.createChannel();
			chl.queueDeclare(fullQueueName, true, false, false, null);

			chl.basicConsume(fullQueueName, true, new DirectDeliveryConsumer(chl, session));

		} catch (IOException e) {
			log.error(e.toString());

		} catch (TimeoutException e) {
			log.error(e.toString());
		}

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		chl.close();
		conn.close();
	}

	public ConnectionFactory getConnectionFactory() {
		// Get enviromental variables
		JSONObject env = new JSONObject(System.getenv("VCAP_SERVICES"));
		JSONArray rmqInstances = env.getJSONArray("rabbitmq");
		JSONObject rmq = rmqInstances.getJSONObject(0);
		JSONObject rmqCredentials = rmq.getJSONObject("credentials");

		// Create connection to rabbit
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(rmqCredentials.getString("username"));
		factory.setPassword(rmqCredentials.getString("password"));
		factory.setHost(rmqCredentials.getString("hostname"));
		factory.setPort(new Integer(rmqCredentials.getString("port")));

		return factory;
	}

}
