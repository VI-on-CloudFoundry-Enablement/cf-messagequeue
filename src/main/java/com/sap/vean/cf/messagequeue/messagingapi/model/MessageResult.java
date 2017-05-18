package com.sap.vean.cf.messagequeue.messagingapi.model;

import java.util.List;

public class MessageResult {

	private boolean status;
	private String statusText;
	private List<String> messages;
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	
	
}
