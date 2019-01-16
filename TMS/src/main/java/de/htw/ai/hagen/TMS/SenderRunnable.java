package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;

import de.htw.ai.hagen.TMS.Controller;
import de.htw.ai.hagen.TMS.Message;
import de.htw.ai.hagen.TMS.Sender;

public class SenderRunnable implements Runnable {

	Serial serial = Controller.serial;
	Message message;

	public SenderRunnable(Message message) {
		this.message = message;
	}

	@Override
	public void run() {
		sendMessage(message);
	}

	private void sendMessage(Message message) {

		try {
			synchronized (Controller.lock2) {
				while (Controller.moduleInUse || !Controller.isConfigured) {
					Controller.lock2.wait();
				}

				Controller.moduleInUse = true;

				synchronized (Controller.lock1) {

					int messageLength = message.toString().length();
					System.out.println("[Sending]: " + message.toString());
					serial.write("AT+SEND=" + messageLength);
					serial.write('\r');
					serial.write('\n');
					Controller.lock1.wait();
				}
				serial.write(message.toString());

				Sender.preparedToSend = false;
				Controller.moduleInUse = false;
			}

		} catch (IllegalStateException | IOException | InterruptedException e) {
			System.out.println("Something went wrong when preparing to send the message:");
			e.printStackTrace();
		}

	}

}
