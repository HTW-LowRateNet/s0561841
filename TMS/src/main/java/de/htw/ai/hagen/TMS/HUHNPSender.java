package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.Random;

import com.pi4j.io.serial.Serial;


/**
 * Class that functions as sender for all 
 * @author Hagen
 *
 */
public class HUHNPSender {
	private Serial serial;
	public static Boolean preparedToSend = false;

	public HUHNPSender(Serial serial) {
		this.serial = serial;
	}

	
	/**
	 * method that sets permanent address of the module to addr
	 * @param addr permanent address 
	 * @return address
	 */
	protected String setPermanentAddress(String addr) {
		String address;
		synchronized (HUHNPController.lock1) {
			address = addr;
			System.out.println("Set own permanent address: " + address);
			this.sendATCommand("AT+ADDR=" + address);
			return address;
		}
	};
	
	/**
	 * method to discover all neighbor nodes
	 */
	protected void discoverNeighbors() {
		System.out.println("Discovering neighbors");
		HUHNPMessage neighborDiscoveryMessage = new HUHNPMessage(MessageCode.DISC, generateMessageID(), 6, 0, "Anyone close enough to cuddle?");
		sendMessage(neighborDiscoveryMessage);
	}
	
	/**
	 * method to discover the PAN coordinator
	 */
	protected void discoverPANCoordinator() {
		System.out.println("Is there a coordinator?");
		HUHNPMessage coordinatorDiscoveryMessage = new HUHNPMessage(MessageCode.KDIS, generateMessageID(), 6, 0, "Is anybody out there? I want to talk to the Captain of this goddamn ship!");
		sendMessage(coordinatorDiscoveryMessage);
	}
	
	protected void sendCoordinatorKeepAlive() {
		HUHNPMessage imTheCaptainMessage = new HUHNPMessage(MessageCode.ALIV, generateMessageID(), 6, 0, "I'm the captain!");
		sendMessage(imTheCaptainMessage);
		
	}
	
	/**
	 * helper method to send HUHNP messages
	 * @param message
	 */
	private void sendMessage(HUHNPMessage message) {
		try {
			int messageLength = message.toString().length();
			System.out.println("AT+SEND=" + messageLength);
			System.out.println("Nachricht: "+message.toString());
			serial.write("AT+SEND=" + messageLength);
			serial.write('\r');
			serial.write('\n');
			// logging to console

			//Waiting for preparedToSend to be true
			while (HUHNPSender.preparedToSend == false) {
				try {
					synchronized (HUHNPController.lock1) {
					HUHNPController.lock1.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			serial.write(message.toString());
			HUHNPSender.preparedToSend = false;
			
			} catch (IllegalStateException | IOException e) {
			System.out.println("Something went wrong when preparing to send the message:");
			e.printStackTrace();
		}

	}
	
	/**
	 * method to send AT commands directly to the module
	 */
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
	 * @return
	 */
	// TODO Generate smarter message ID
	private String generateMessageID () {
		Random random = new Random();
		Integer number = random.nextInt(8999)+1000;
		System.out.println("Don't forget to change the messageID generator!");
		return number.toString()+"mID";
	}
	
	/**
	 * configures the module initially on startup.
	 * @throws InterruptedException
	 */
	protected void configureModule() throws InterruptedException {
		synchronized (HUHNPController.lock1) {
			this.sendATCommand("AT+CFG=433000000,20,9,10,1,1,0,0,0,0,3000,8,4");
			HUHNPController.lock1.wait();
		}
		this.setTemporaryAddress();
	};

	protected String setTemporaryAddress() {
		String address;
		synchronized (HUHNPController.lock1) {
			address = AddressSpaces.createTemporaryNodeAddress();
			System.out.println("Set own temporary address: " + address);
			this.sendATCommand("AT+ADDR=" + address);
			return address;
		}
	};

}
