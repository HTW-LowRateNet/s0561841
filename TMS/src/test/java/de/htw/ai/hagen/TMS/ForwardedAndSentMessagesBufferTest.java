package de.htw.ai.hagen.TMS;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ForwardedAndSentMessagesBufferTest {
	
	@Test
	public void containsTest() {
		
		MessageBuffer forwardbuffer = new  MessageBuffer();
		
		forwardbuffer.addForwardedMessage(new Message(MessageCode.CDIS, "xyz",Controller.address, "0000",
				"Looking for the coordinator."));
		
		assertTrue(forwardbuffer.contains(new Message(MessageCode.CDIS, "xyz",Controller.address, "0000",
				"Looking for the coordinatoooor.")));
	}
	

}
