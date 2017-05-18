package com.sap.vean.cf.samples.model;

import java.util.List;

public class ResultData {

	private boolean success;
	private String message = "";
	private List<RabbitMessage> rabbitMessages;

	 
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<RabbitMessage> getRabbitMessages() {
		return rabbitMessages;
	}
	public void setRabbitMessages(List<RabbitMessage> rabbitMessages) {
		this.rabbitMessages = rabbitMessages;
	}

	
	
}
