package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;

public class SerialInputListener implements Runnable {

	Serial serial;
	HUHNPInterpreter interpreter;
	
	public SerialInputListener(Serial serial, HUHNPInterpreter interpreter) {
		this.serial = serial;
		this. interpreter = interpreter;
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
					interpreter.parseIncomingData(event.getAsciiString().toString());
//					synchronized (HUHNPController.lock1) {
//						//System.out.println("[Serial input] " + event.getAsciiString());
//						
//						//System.out.println("[HEX DATA]   " + event.getHexByteString());
//						
//						//HUHNPController.lock1.notify();
//					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
