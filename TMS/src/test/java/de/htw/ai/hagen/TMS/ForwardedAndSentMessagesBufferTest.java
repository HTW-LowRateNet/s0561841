package de.htw.ai.hagen.TMS;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ForwardedAndSentMessagesBufferTest {
	
	@Test
	public void containsTest() {
		
		ForwardedAndSentMessagesBuffer forwardbuffer = new  ForwardedAndSentMessagesBuffer();
		
		forwardbuffer.addForwardedMessage(new HUHNPMessage(MessageCode.CDIS, "xyz",HUHNPController.address, "0000",
				"Looking for the coordinator."));
		
		assertTrue(forwardbuffer.contains(new HUHNPMessage(MessageCode.CDIS, "xyz",HUHNPController.address, "0000",
				"Looking for the coordinatoooor.")));
	}
	

}
