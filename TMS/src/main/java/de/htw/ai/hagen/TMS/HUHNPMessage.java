package de.htw.ai.hagen.TMS;

import java.text.ParseException;

public class HUHNPMessage {
	private String code;
	private String payload;
	private String messageId;
	private String sourceAddress;
	private String destinationAddress;
	private String timeToLive = "06";
	private String currentHops = "01";

	// Constructor for devs using predefined message codes
	public HUHNPMessage(MessageCode mCode, String messageId, String timeToLive, String currentHops, String sourceAddress,
			String destination, String payload) {
		this.code = mCode.code;
		this.messageId = messageId;
		this.timeToLive = timeToLive;
		this.currentHops = currentHops;
		this.payload = payload;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destination;
	}
	
	// Constructor for devs using predefined message codes with default TTL and hops
	public HUHNPMessage(MessageCode mCode, String messageId, String sourceAddress,
			String destination, String payload) {
		this.code = mCode.code;
		this.messageId = messageId;
		this.payload = payload;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destination;
	}
	
	// private constructor for parseToHUHNPMessage
	private HUHNPMessage(String mCode, String messageId, String timeToLive, String currentHops, String sourceAddress,
			String destination, String payload) {
		this.code = mCode;
		this.messageId = messageId;
		this.timeToLive = timeToLive;
		this.currentHops = currentHops;
		this.payload = payload;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destination;
	}

	@Override
	public String toString() {
		return code + "," + messageId + "," + timeToLive + "," + currentHops + "," + sourceAddress + ","
				+ destinationAddress + "," + payload;
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

	public String getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(String timeToLive) {
		this.timeToLive = timeToLive;
	}

	public String getCurrentHops() {
		return currentHops;
	}

	public void setCurrentHops(String currentHops) {
		this.currentHops = currentHops;
	}
	
	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public static HUHNPMessage parseToHUHNPMessage  (String message) throws IndexOutOfBoundsException {
		int mcodeDelimiter = message.indexOf(",", 0);
		int mIdDelimiter  = message.indexOf(",", mcodeDelimiter+1);
		int timeToLiveDelimiter = message.indexOf(",", mIdDelimiter+1);
		int currentHopsDelimiter = message.indexOf(",", timeToLiveDelimiter+1);
		int sourceAddressDelimiter = message.indexOf(",", currentHopsDelimiter+1);
		int destinationDelimiter = message.indexOf(",", sourceAddressDelimiter+1);
		
		String mCode = message.substring(0, mcodeDelimiter);
		String mId = message.substring(mcodeDelimiter+1,mIdDelimiter);
		String timeToLive = message.substring(mIdDelimiter+1,timeToLiveDelimiter);
		String currentHops = message.substring(timeToLiveDelimiter+1,currentHopsDelimiter);
		String sourceAddress = message.substring(currentHopsDelimiter+1,sourceAddressDelimiter);
		String destination = message.substring(sourceAddressDelimiter+1,destinationDelimiter);
		String payload = (message.length()>=destinationDelimiter+2) ? message.substring(destinationDelimiter+1) : "";
		
		return new HUHNPMessage(mCode, mId, timeToLive, currentHops, sourceAddress, destination, payload);
	}

}
