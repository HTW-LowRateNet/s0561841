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
			while (SimpleSender.preparedToSend == false) {

				try {
					synchronized (HUHNPController.lock1) {
						int messageLength = message.toString().length();
						// System.out.println("AT+SEND=" + messageLength);
						System.out.println("[Sending]: " + message.toString());
						serial.write("AT+SEND=" + messageLength);
						serial.write('\r');
						serial.write('\n');
						HUHNPController.lock1.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			serial.write(message.toString());

			// remove message from message queue and restore booleans
//				messageQueue.remove(message);
//			busy = false;
			SimpleSender.preparedToSend = false;

		} catch (IllegalStateException | IOException e) {
			System.out.println("Something went wrong when preparing to send the message:");
			e.printStackTrace();
		}

//			// if there are more messages waiting to be sent, do it.
//			if (!messageQueue.isEmpty()) {
//				sendMessage(messageQueue.iterator().next());
//			}
//
//	}

	}

}
