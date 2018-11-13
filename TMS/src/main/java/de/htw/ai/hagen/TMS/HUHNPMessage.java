package de.htw.ai.hagen.TMS;

public class HUHNPMessage {
	private String code;
	private String payload;
	private String messageId;
	private int timeToLive;
	private int currentHops = 0;

	public HUHNPMessage(MessageCode mCode, String messageId, int timeToLive, int currentHops, String payload) {
		this.code = mCode.code;
		this.messageId = messageId;
		this.timeToLive = timeToLive;
		this.currentHops = currentHops;
		this.payload = payload;
	}

	@Override
	public String toString() {
		return code + messageId + timeToLive + currentHops + payload;
	}

	
	// Setters and Getters
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public int getCurrentHops() {
		return currentHops;
	}

	public void setCurrentHops(int currentHops) {
		this.currentHops = currentHops;
	}

}
