package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.Random;
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
	private final static String BROADCAST_ADDRESS = "FFFF";
	private final static String DEFAULT_NUMBER_OF_HOPS = "06";

	public SimpleSender(Serial serial) {
		this.serial = serial;
	}

	/**
	 * helper method to send HUHNP messages
	 * 
	 * @param message
	 */
	private void sendMessage(HUHNPMessage message) {
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
		synchronized (HUHNPController.lock1) {
			address = addr;
			System.out.println("Set own permanent address: " + address);
			HUHNPController.addressIsPermanent = true;
			this.sendATCommand("AT+ADDR=" + address);
			return address;
		}
	};

	/**
	 * method to discover all neighbor nodes
	 */
	protected synchronized void discoverNeighbors() {
		System.out.println("Discovering neighbors");
		HUHNPMessage neighborDiscoveryMessage = new HUHNPMessage(MessageCode.DISC, generateMessageID(), "06", "00",
				HUHNPController.address, BROADCAST_ADDRESS, "Anyone close enough to cuddle?");
		sendMessage(neighborDiscoveryMessage);
	}

	/** method to discover the PAN coordinator */
	protected synchronized void discoverPANCoordinator() {
		HUHNPMessage coordinatorDiscoveryMessage = new HUHNPMessage(MessageCode.CDIS, generateMessageID(), "06", "00",
				HUHNPController.address, BROADCAST_ADDRESS,
				"Is anybody out there? I want to talk to the Captain of this goddamn ship!");
		sendMessage(coordinatorDiscoveryMessage);
	}

	/** Method to let network know this node is the coordinator */
	protected synchronized void sendCoordinatorKeepAlive() {
		HUHNPMessage imTheCaptainMessage = new HUHNPMessage(MessageCode.ALIV, generateMessageID(), "06", "00",
				HUHNPController.address, BROADCAST_ADDRESS, "I'm the captain!");
		sendMessage(imTheCaptainMessage);
	}

	/** Method to let the network know it needs to reset itself */
	public void sendNetworkReset() {
		HUHNPMessage networkResetMessage = new HUHNPMessage(MessageCode.NRST, generateMessageID(), "06", "00",
				HUHNPController.address, BROADCAST_ADDRESS, "Network restart needed");
		sendMessage(networkResetMessage);
	};

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
		Random random = new Random();
		Integer number = random.nextInt(8999) + 1000;
		System.out.println("Don't forget to change the messageID generator!");
		return number.toString() + "mID";
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
		HUHNPController.isConfigured = true;
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

	/**
	 * Method that allows this node to send an address in Response to an ADDR request
	 * @param receiver
	 * @param messageId
	 * @param address
	 */
	public void sendAddress(String receiver, String messageId, String address) {
		HUHNPMessage newAddressMessage = new HUHNPMessage(MessageCode.ADDR, messageId, "06", "00",
				HUHNPController.address, receiver, address);
		sendMessage(newAddressMessage);
	}
	
	/**
	 * Method to make an ADDR request to the coordinator
	 */
	public void requestAddress() {
		HUHNPMessage newAddressMessage = new HUHNPMessage(MessageCode.ADDR, generateMessageID(), "06", "00",
				HUHNPController.address, "0000", "");
		sendMessage(newAddressMessage);
	}

}
