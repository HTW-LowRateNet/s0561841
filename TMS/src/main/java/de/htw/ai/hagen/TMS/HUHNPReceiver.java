package de.htw.ai.hagen.TMS;

import com.pi4j.io.serial.Serial;

public class HUHNPReceiver {
	private Serial serial;

	public HUHNPReceiver(Serial serial) {
		this.serial = serial;
	}

	public static void sendMessage(HUHNPMessage message) {
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

	}

}
