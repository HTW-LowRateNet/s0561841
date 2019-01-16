package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;

public class SerialInputListener implements Runnable {

	Serial serial;
	MessageInterpreter interpreter;

	public SerialInputListener(Serial serial, MessageInterpreter interpreter) {
		this.serial = serial;
		this.interpreter = interpreter;
	}

	@Override
	public void run() {
		// create and register the serial data listener
		serial.addListener(new SerialDataEventListener() {
			public void dataReceived(SerialDataEvent event) {
				// print out the data received to the console
				try {
					interpreter.parseIncomingData(event.getAsciiString().toString());

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
