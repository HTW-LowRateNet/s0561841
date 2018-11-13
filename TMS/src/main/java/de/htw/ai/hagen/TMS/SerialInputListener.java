package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;

public class SerialInputListener implements Runnable {

	Serial serial;

	public SerialInputListener(Serial serial) {
		this.serial = serial;
	}

	@Override
	public void run() {
		// create and register the serial data listener
		serial.addListener(new SerialDataEventListener() {
			public void dataReceived(SerialDataEvent event) {

				// NOTE! - It is extremely important to read the data received from the
				// serial port. If it does not get read from the receive buffer, the
				// buffer will continue to grow and consume memory.

				// print out the data received to the console
				try {
					if (HUHNPInterpreter.parseIncomingData(event.getAsciiString())) {
						HUHNPInterpreter.gotExpectedAnswerFromModule = true; // needs to be set to false after sending
					}
					synchronized (HUHNPController.lock1) {
						if (HUHNPInterpreter.parseIncomingData(event.getAsciiString())) {
							HUHNPInterpreter.gotExpectedAnswerFromModule = true; // needs to be set to false after
																					// sending
						}
						System.out.println("[HEX DATA]   " + event.getHexByteString());
						System.out.println("[ASCII DATA] " + event.getAsciiString());
						HUHNPController.lock1.notify();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
