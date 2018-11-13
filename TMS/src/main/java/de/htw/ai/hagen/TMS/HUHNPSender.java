package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;

public class HUHNPSender {
	private static Serial serial;
	public static Boolean preparedToSend = false;

	public HUHNPSender(Serial serial) {
		HUHNPSender.serial = serial;
	}

	public void sendMessage(HUHNPMessage message) {
		/*
		 * TODO - see if currentHops = timeToLive. if yes, don't send new message -
		 * either set
		 */

		// switch/case currently does nothing. Just here if needed in future
		switch (message.getCode()) {
		case "DISC":
			break;
		case "KDIS":
			break;
		case "ADDR":

			break;
		case "MSSG":
			break;
		case "POLL":
			break;
		default:
			System.out.println("ERROR: Unknown Message code: " + message.getCode());
			break;
		}
		try {
			serial.write(message.toString());
			System.out.println("Sending the following message: " + message);
		} catch (IllegalStateException | IOException e) {
			System.out.println("Something went wrong when sending the message:");
			e.printStackTrace();
		}
	}

	public void prepareForSending(HUHNPMessage message) {
		try {
			String messageLengthAsHexadecimal = Integer.toHexString(message.toString().length());
			serial.write("AT+SEND=" + messageLengthAsHexadecimal);
			serial.write('\r');
			serial.write('\n');
			// logging to console
			System.out.println("AT+SEND=" + messageLengthAsHexadecimal);
			while (HUHNPSender.preparedToSend == false) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IllegalStateException | IOException e) {
			System.out.println("Something went wrong when preparing to send the message:");
			e.printStackTrace();
		}

	}

}
