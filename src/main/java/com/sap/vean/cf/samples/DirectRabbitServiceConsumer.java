package com.sap.vean.cf.samples;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sap.vean.cf.samples.model.ResultData;

@Controller
@EnableAutoConfiguration
@RequestMapping("/dsrc")
public class DirectRabbitServiceConsumer {

	private static final Logger log = LoggerFactory.getLogger(DirectRabbitServiceConsumer.class);

	private static final String QUEUE_NAME = "rabbit-string-test";
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResultData getData() {

		log.debug("Running Service");

		ResultData rs = new ResultData();
		
		try {
			
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
			Connection conn = factory.newConnection();
	
			//Create channel
			Channel chl = conn.createChannel();			
			chl.queueDeclare(QUEUE_NAME, true, false, false, null);			
			
			//Create and Publish Message
			byte[] messageBodyBytes = ("Hello, world!" + System.currentTimeMillis()).getBytes("UTF-8");
			chl.basicPublish("", QUEUE_NAME, null, messageBodyBytes);
			log.info("Message sent: " + messageBodyBytes);
			
			//Get Message
			String message = new String(chl.basicGet(QUEUE_NAME, false).getBody(), "UTF-8");
			
			//Create Return Message
			rs.setMessage("Cloud Foundry Rabbit Example: Message Received: " + message);
			rs.setSuccess(true);
	
			chl.close();
			conn.close();
			
		} catch (IOException e) {
			log.error(e.toString());
			
			rs.setMessage("Error " + e.toString() + " " + e.getLocalizedMessage());
			rs.setSuccess(false);

		} catch (TimeoutException e) {
			log.error(e.toString());
			rs.setMessage("Error " + e.toString());
			rs.setSuccess(false);
		}

		return rs;

	}
}
