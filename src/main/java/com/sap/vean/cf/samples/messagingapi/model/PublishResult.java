package com.sap.vean.cf.samples.messagingapi.model;

import java.util.List;

public class PublishResult {

	private boolean status;
	private String statusText;
	
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
	
}
