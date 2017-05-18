package com.sap.vean.cf.samples.messagingapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.sap.vean.cf.samples.messagingapi.model.MessageResult;
import com.sap.vean.cf.samples.messagingapi.model.PublishResult;

@Controller
@EnableAutoConfiguration
@RequestMapping("/messaging/{queue}")
public class MessagingAPIService {

	private static final Logger log = LoggerFactory.getLogger(MessagingAPIService.class);
	public static final String QUEUE_PREFIX = "gms-";
	
	public ConnectionFactory getConnectionFactory() {
		
		//Get enviromental variables 
		JSONObject env = new JSONObject(System.getenv("VCAP_SERVICES"));
		JSONArray rmqInstances = env.getJSONArray("rabbitmq");	
		JSONObject rmq = rmqInstances.getJSONObject(0);
		JSONObject rmqCredentials = rmq.getJSONObject("credentials");

		//Create connection to rabbit
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(rmqCredentials.getString("username"));
		factory.setPassword(rmqCredentials.getString("password"));
		factory.setHost(rmqCredentials.getString("hostname"));
		factory.setPort(new Integer(rmqCredentials.getString("port")));	
		
		return factory;
	}
	
	@RequestMapping(path = "/consume", method = RequestMethod.GET)
	public @ResponseBody MessageResult consumeAllMessage(@PathVariable("queue") String queue, @RequestParam(required=false,name="ack") String ack, @RequestParam(required=false,name="fullresponse") String fullresponse) {

		log.debug("Running Service");
	
		MessageResult result = new MessageResult();
		String fullQueueName = QUEUE_PREFIX + queue;
		
		boolean ackB = true;
		if(ack != null && ack.equals("false")){
			ackB = false;
		}
		
		boolean fullResp = false;
		if(fullresponse != null && fullresponse.equals("true")){
			fullResp = true;
		}
		
		try {
						
			Connection conn = getConnectionFactory().newConnection();
	
			//Create channel and declare queue
			Channel chl = conn.createChannel();			
			chl.queueDeclare(fullQueueName, true, false, false, null);			
			
			GetResponse resp = null;
			ArrayList<String> messages = new ArrayList<String>();
			
			//Get Message
			do{
				resp = chl.basicGet(fullQueueName, ackB);
				if(resp != null){
					if(fullResp){
						messages.add(resp.toString());
					} else {
						String message = new String(resp.getBody(), "UTF-8");
						messages.add(message);
					}
				} else {
					break;
				}
				
			}while(true);
			
			//Create Return Message
			result.setMessages(messages);
			result.setStatusText("Messages consumed from queue " + fullQueueName);;
			result.setStatus(true);
			
			chl.close();
			conn.close();
			
		} catch (IOException e) {
			log.error(e.toString());			
			result.setStatusText("Error " + e.toString() + " " + e.getLocalizedMessage());
			result.setStatus(false);

		} catch (TimeoutException e) {
			log.error(e.toString());
			result.setStatusText("Error " + e.toString() + " " + e.getLocalizedMessage());
			result.setStatus(false);
		}

		return result;

	}
	
	@RequestMapping(path = "/publish", method = RequestMethod.POST)
	public @ResponseBody PublishResult publishMessage(@PathVariable("queue") String queue, @RequestBody String message) {

		log.debug("Running Service");
	
		PublishResult rs = new PublishResult();
		String fullQueueName = QUEUE_PREFIX + queue;
		
		try {
						
			Connection conn = getConnectionFactory().newConnection();
	
			//Create channel and declare queue
			Channel chl = conn.createChannel();			
			chl.queueDeclare(fullQueueName, true, false, false, null);			
			
			//Create and Publish Message
			byte[] messageBodyBytes = message.getBytes("UTF-8");
			chl.basicPublish("", fullQueueName, null, messageBodyBytes);
			
			
			//Create Return Message
			rs.setStatus(true);
			rs.setStatusText("Message put on queue " + fullQueueName);;
	
			chl.close();
			conn.close();
			
		} catch (IOException e) {
			log.error(e.toString());			
			rs.setStatusText("Error " + e.toString() + " " + e.getLocalizedMessage());
			rs.setStatus(false);

		} catch (TimeoutException e) {
			log.error(e.toString());
			rs.setStatusText("Error " + e.toString() + " " + e.getLocalizedMessage());
			rs.setStatus(false);
		}

		return rs;

	}
	
	
}
