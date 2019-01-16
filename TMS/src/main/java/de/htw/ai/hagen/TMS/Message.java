package de.htw.ai.hagen.TMS;

import java.text.ParseException;

public class Message {
	private String code;
	private String payload;
	private String messageId;
	private String sourceAddress;
	private String destinationAddress;
	private String timeToLive = "06";
	private String currentHops = "00";

	// Constructor for devs using predefined message codes
	public Message(MessageCode mCode, String messageId, String timeToLive, String currentHops, String sourceAddress,
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
	public Message(MessageCode mCode, String messageId, String sourceAddress,
			String destination, String payload) {
		this.code = mCode.code;
		this.messageId = messageId;
		this.payload = payload;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destination;
	}
	
	// private constructor for parseToHUHNPMessage
	private Message(String mCode, String messageId, String timeToLive, String currentHops, String sourceAddress,
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

	public static Message parseToHUHNPMessage  (String message) throws IndexOutOfBoundsException {
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
		
		return new Message(mCode, mId, timeToLive, currentHops, sourceAddress, destination, payload);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((currentHops == null) ? 0 : currentHops.hashCode());
		result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + ((sourceAddress == null) ? 0 : sourceAddress.hashCode());
		result = prime * result + ((timeToLive == null) ? 0 : timeToLive.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (currentHops == null) {
			if (other.currentHops != null)
				return false;
		} else if (!currentHops.equals(other.currentHops))
			return false;
		if (destinationAddress == null) {
			if (other.destinationAddress != null)
				return false;
		} else if (!destinationAddress.equals(other.destinationAddress))
			return false;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (sourceAddress == null) {
			if (other.sourceAddress != null)
				return false;
		} else if (!sourceAddress.equals(other.sourceAddress))
			return false;
		if (timeToLive == null) {
			if (other.timeToLive != null)
				return false;
		} else if (!timeToLive.equals(other.timeToLive))
			return false;
		return true;
	}

}
