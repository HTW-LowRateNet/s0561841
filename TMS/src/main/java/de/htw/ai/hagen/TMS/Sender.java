package de.htw.ai.hagen.TMS;

import java.io.IOException;
import com.pi4j.io.serial.Serial;

/**
 * Class that functions as sender for all HUHNP messages as well as sending AT
 * commands to the module
 * 
 * @author Hagen
 *
 */
public class Sender {
	private Serial serial;

	public static Boolean preparedToSend = false;
	public static int messageId = 0;
	private final static String COORDINATOR_ADDRESS = "0000";
	private final static String BROADCAST_ADDRESS = "FFFF";
	private final static String DEFAULT_NUMBER_OF_HOPS = "06";

	public Sender(Serial serial) {
		this.serial = serial;
	}

	/**
	 * method to send and forward messages
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {
		Controller.forwardedMessageBuffer.addForwardedMessage(message);
		Thread sendingMessage = new Thread(new SenderRunnable(message));
		sendingMessage.start();

	}

	/** method to discover the PAN coordinator */
	protected synchronized void discoverPANCoordinator() {
		Message coordinatorDiscoveryMessage = new Message(MessageCode.CDIS, generateMessageID(), Controller.address,
				COORDINATOR_ADDRESS, "Looking for the coordinator.");
		sendMessage(coordinatorDiscoveryMessage);
	}

	/** Method to let network know this node is the coordinator */
	protected synchronized void sendCoordinatorKeepAlive() {
		Message imTheCaptainMessage = new Message(MessageCode.ALIV, generateMessageID(), Controller.address,
				BROADCAST_ADDRESS, "Call me daddy. (Hagen is coordinator)");
		sendMessage(imTheCaptainMessage);
	}

	/**
	 * Method to make an ADDR request to the coordinator
	 */
	public void requestAddress() {
		Message newAddressMessage = new Message(MessageCode.ADDR, generateMessageID(), Controller.address,
				COORDINATOR_ADDRESS, "");
		sendMessage(newAddressMessage);
	}

	/**
	 * Method that allows this node to send an address in Response to an ADDR
	 * request
	 * 
	 * @param receiver
	 * @param messageId
	 * @param address
	 */
	public void sendAddress(String receiver, String messageId, String address) {
		Message newAddressMessage = new Message(MessageCode.ADDR, messageId, Controller.address, receiver, address);
		sendMessage(newAddressMessage);
	}

	/**
	 * Method to send an AACK to the coordinator
	 */
	public void sendAAcknowledgement(Message message) {
		Message newAACKMessage = new Message(MessageCode.AACK, generateMessageID(), Controller.address,
				message.getSourceAddress(), "Thx, bye.");
		sendMessage(newAACKMessage);
	}

	/** Method to let the network know it needs to reset itself */
	public void sendNetworkReset() {
		Message networkResetMessage = new Message(MessageCode.NRST, generateMessageID(), Controller.address,
				BROADCAST_ADDRESS, "Network restart needed");
		sendMessage(networkResetMessage);
	}

	/**
	 * method that sets permanent address of the module to addr
	 * 
	 * @param addr permanent address
	 * @return address
	 */
	protected String setPermanentAddress(String addr) {
		String address;
		synchronized (Controller.lock2) {
			address = addr;
			System.out.println("Set own permanent address: " + address);
			Controller.addressIsPermanent = true;
			Controller.isConfigured = false;
			this.sendATCommand("AT+ADDR=" + address);
		}
		return address;
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
		messageId += 1;
		return "id-" + messageId;
	}

	/**
	 * configures the module initially on startup.
	 * 
	 * @throws InterruptedException
	 */
	protected void configureModule() throws InterruptedException {
		synchronized (Controller.lock1) {
			this.sendATCommand("AT+CFG=433000000,20,9,10,1,1,0,0,0,0,3000,8,4");
			Controller.lock1.wait();
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
		synchronized (Controller.lock1) {
			address = AddressManager.createTemporaryNodeAddress();
			System.out.println("Set own temporary address: " + address);
			Controller.address = address;
			this.sendATCommand("AT+ADDR=" + address);
			return address;
		}
	}

}
