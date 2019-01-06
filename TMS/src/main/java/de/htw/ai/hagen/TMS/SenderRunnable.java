package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;

import de.htw.ai.hagen.TMS.HUHNPController;
import de.htw.ai.hagen.TMS.HUHNPMessage;
import de.htw.ai.hagen.TMS.SimpleSender;

public class SenderRunnable implements Runnable {

	Serial serial = HUHNPController.serial;
	HUHNPMessage message;

	public SenderRunnable(HUHNPMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		sendMessage(message);
	}

	private void sendMessage(HUHNPMessage message) {

		try {
			synchronized (HUHNPController.lock2) {
				while (HUHNPController.moduleInUse || !HUHNPController.isConfigured) {
					HUHNPController.lock2.wait();
				}

				HUHNPController.moduleInUse = true;

				synchronized (HUHNPController.lock1) {

					int messageLength = message.toString().length();
					System.out.println("[Sending]: " + message.toString());
					serial.write("AT+SEND=" + messageLength);
					serial.write('\r');
					serial.write('\n');
					HUHNPController.lock1.wait();
				}
				serial.write(message.toString());

				SimpleSender.preparedToSend = false;
				HUHNPController.moduleInUse = false;
			}

		} catch (IllegalStateException | IOException | InterruptedException e) {
			System.out.println("Something went wrong when preparing to send the message:");
			e.printStackTrace();
		}

	}

}
