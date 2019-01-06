package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi4j.io.serial.Serial;

/**
 * Class that functions as sender for all HUHNP messages as well as sending AT
 * commands to the module
 * 
 * @author Hagen
 *
 */
public class SimpleSender {
	private Serial serial;

	public static Boolean preparedToSend = false;
	public static int messageId = 0;
	private final static String COORDINATOR_ADDRESS = "0000";
	private final static String BROADCAST_ADDRESS = "FFFF";
	private final static String DEFAULT_NUMBER_OF_HOPS = "06";
	public static Queue<String> messageQueue = new LinkedBlockingQueue<>();

	

	public SimpleSender(Serial serial) {
		this.serial = serial;
	}

	/**
	 * method to send and forward HUHNP messages
	 * 
	 * @param message
	 */
	public void sendMessage(HUHNPMessage message) {
		HUHNPController.forwardedMessageBuffer.addForwardedMessage(message);
		messageQueue.add(message.getMessageId());
		Thread sendingMessage = new Thread(new SenderRunnable(message));
		sendingMessage.start();

	}

	/**
	 * method that sets permanent address of the module to addr
	 * 
	 * @param addr permanent address
	 * @return address
	 */
	protected String setPermanentAddress(String addr) {
		String address;
		synchronized (HUHNPController.lock2) {
			address = addr;
			System.out.println("Set own permanent address: " + address);
			HUHNPController.addressIsPermanent = true;
			HUHNPController.isConfigured = false;
			this.sendATCommand("AT+ADDR=" + address);
		}
			return address;
		
	};

	/** method to discover the PAN coordinator */
	protected synchronized void discoverPANCoordinator() {
		HUHNPMessage coordinatorDiscoveryMessage = new HUHNPMessage(MessageCode.CDIS, generateMessageID(),
				HUHNPController.address, COORDINATOR_ADDRESS,
				"Looking for the coordinator.");
		sendMessage(coordinatorDiscoveryMessage);
	}

	/** Method to let network know this node is the coordinator */
	protected synchronized void sendCoordinatorKeepAlive() {
		HUHNPMessage imTheCaptainMessage = new HUHNPMessage(MessageCode.ALIV, generateMessageID(),
				HUHNPController.address, BROADCAST_ADDRESS, "They call me the coordinator.");
		sendMessage(imTheCaptainMessage);
	}

	/** Method to let the network know it needs to reset itself */
	public void sendNetworkReset() {
		HUHNPMessage networkResetMessage = new HUHNPMessage(MessageCode.NRST, generateMessageID(),
				HUHNPController.address, BROADCAST_ADDRESS, "Network restart needed");
		sendMessage(networkResetMessage);
	};
	
	/**
	 * Method that allows this node to send an address in Response to an ADDR request
	 * @param receiver
	 * @param messageId
	 * @param address
	 */
	public void sendAddress(String receiver, String messageId, String address) {
		HUHNPMessage newAddressMessage = new HUHNPMessage(MessageCode.ADDR, messageId,
				HUHNPController.address, receiver, address);
		sendMessage(newAddressMessage);
	}
	
	/**
	 * Method to make an ADDR request to the coordinator
	 */
	public void requestAddress() {
		HUHNPMessage newAddressMessage = new HUHNPMessage(MessageCode.ADDR, generateMessageID(),
				HUHNPController.address, COORDINATOR_ADDRESS, "");
		sendMessage(newAddressMessage);
	}

	/**
	 * Method to send an AACK to the coordinator
	 */
	public void sendAAcknowledgement(HUHNPMessage message) {
		HUHNPMessage newAACKMessage = new HUHNPMessage(MessageCode.AACK, generateMessageID(),
				HUHNPController.address, message.getSourceAddress(), message.getPayload());
		sendMessage(newAACKMessage);
		
	}

	/** method to send AT commands directly to the module */
	protected void sendATCommand(String command) {
		try {
			serial.write(command);
			serial.write('\r');
			serial.write('\n');

			// logging to console
			System.out.println("Sent Command: " + command);
		} catch (IllegalStateException | IOException e) {
			System.out.println("Error sending AT command: " + command);
			e.printStackTrace();
		}
	};

	/**
	 * messageID generator
	 * 
	 * @return
	 */
	// TODO Generate smarter message ID
	private String generateMessageID() {
		messageId += 1;
		return "id-"+messageId;
	}

	/**
	 * configures the module initially on startup.
	 * 
	 * @throws InterruptedException
	 */
	protected void configureModule() throws InterruptedException {
		synchronized (HUHNPController.lock1) {
			this.sendATCommand("AT+CFG=433000000,20,9,10,1,1,0,0,0,0,3000,8,4");
			HUHNPController.lock1.wait();
		}
		this.setTemporaryAddress();
		
	};

	/**
	 * sets temporary address of this node if there's a network coordinator and this
	 * node needs a permanent address
	 * 
	 * @return address from the reserved temporary address space
	 */
	protected String setTemporaryAddress() {
		String address;
		synchronized (HUHNPController.lock1) {
			address = AddressManager.createTemporaryNodeAddress();
			System.out.println("Set own temporary address: " + address);
			HUHNPController.address=address;
			this.sendATCommand("AT+ADDR=" + address);
			return address;
		}
	}



}
