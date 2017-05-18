package com.sap.vean.cf.samples;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.vean.cf.samples.model.RabbitMessage;
import com.sap.vean.cf.samples.model.ResultData;

@Controller
@EnableAutoConfiguration
@RequestMapping("/sbrc")
public class SpringBasedRabbitConsumer {

	private static final Logger log = LoggerFactory.getLogger(SpringBasedRabbitConsumer.class);

	 @Autowired
	    private RabbitTemplate template;

	    private static final String QUEUE_NAME = "rabbit-message-test";
	    

	    @Bean
	    Queue queue() {
	        return new Queue(QUEUE_NAME, false);
	    }

	    @RequestMapping(method = RequestMethod.GET)
	    public @ResponseBody ResultData doTestMessageAndReturn() {

	    	String message = "rabbitmq-test" + System.currentTimeMillis();
	        RabbitMessage rabbitMessage = new RabbitMessage();

	        log.info("Sending message: " + message);
	        template.convertAndSend(QUEUE_NAME, message);
	        rabbitMessage.setProducedMessage(message);

	        log.info("Receiving message: ");
	        String msg = (String) template.receiveAndConvert(QUEUE_NAME);
	        rabbitMessage.setConsumedMessage(msg);

	        ResultData result = new ResultData();
	        result.setSuccess(true);
	        result.setMessage("Successfully connected to rabbit and produced a message and consumed the same message");
	        List<RabbitMessage> rabbitMessages = new ArrayList<RabbitMessage>();
	        rabbitMessages.add(rabbitMessage);
	        result.setRabbitMessages(rabbitMessages);

	        
	        return result;
	    }
}
