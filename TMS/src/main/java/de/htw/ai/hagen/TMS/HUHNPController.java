package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.List;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPort;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

public class HUHNPController {
	final static Console console = new Console();
	public static Boolean lock1 = true;

	final static Serial serial = SerialFactory.createInstance(); // create an instance of the serial communications
																	// class
	final static HUHNPSender sender = new HUHNPSender(serial);// create an instance of the Sender class
	String address; // Current address of this node
	boolean isCoordinator = false; // Is this node the coordinator in the current network?
	List<String> neighbors; // List of known neighbors
	List<String> allNodesInNetwork; // If this node is coordinator, it should keep a record of all nodes currently
									// in the network

	/**
	 * This example program supports the following optional command
	 * arguments/options: "--device (device-path)" [DEFAULT: /dev/ttyAMA0] "--baud
	 * (baud-rate)" [DEFAULT: 38400] "--data-bits (5|6|7|8)" [DEFAULT: 8] "--parity
	 * (none|odd|even)" [DEFAULT: none] "--stop-bits (1|2)" [DEFAULT: 1]
	 * "--flow-control (none|hardware|software)" [DEFAULT: none]
	 *
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void runHUHNPController(String[] args) throws InterruptedException, IOException {

		// create Pi4J console wrapper/helper
		// (This is a utility class to abstract some of the boilerplate code)

		// print program title/header
		console.title("<-- HUHN-P Project -->", "Technik Mobiler Systeme");

		// allow for user to exit program using CTRL-C
		console.promptForExit();

		// create and register the serial data listener
		Thread serialListener = new Thread(new SerialInputListener(serial));
		Thread userInputListener = new Thread(new UserInputListener(this));
		serialListener.start();
		userInputListener.start();
		
		try {
			// create serial config object
			SerialConfig config = new SerialConfig();

			// set default serial settings (device, baud rate, flow control, etc)
			//
			// by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO
			// header)
			// NOTE: this utility method will determine the default serial port for the
			// detected platform and board/model. For all Raspberry Pi models
			// except the 3B, it will return "/dev/ttyAMA0". For Raspberry Pi
			// model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
			// environment configuration.
			config.device(SerialPort.getDefaultPort()).baud(Baud._115200).dataBits(DataBits._8).parity(Parity.NONE)
					.stopBits(StopBits._1).flowControl(FlowControl.NONE);

			// parse optional command argument options to override the default serial
			// settings.
			if (args.length > 0) {
				config = CommandArgumentParser.getSerialConfig(config, args);
			}

			// display connection details
			console.box(" Connecting to: " + config.toString(),
					" We are sending ASCII data on the serial port every 1 second.",
					" Data received on serial port will be displayed below.");

			// open the default serial device/port with the configuration settings
			serial.open(config);

			this.configureModule();

			// continuous loop to keep the program running until the user terminates the
			// program
			while (console.isRunning()) {

				try {
					
				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				}

				// wait 1 second before continuing
				Thread.sleep(1000);
			}

		} catch (IOException ex) {
			console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
			return;
		}

	}

	// Helper functions
	private synchronized void sendMessage(HUHNPMessage message) {
//		sender.prepareForSending(message);
//		while (HUHNPSender.preparedToSend == false) {
//			wait();
//			sender.sendMessage(message);
//		}
	}
	// TODO make thread wait until Sending OK. Interrupt if exception

	private void configureModule() throws InterruptedException {
		synchronized (lock1) {
			this.sendATCommand("AT+CFG=433000000,20,6,12,1,1,0,0,0,0,3000,8,4");
			lock1.wait();
		}
		System.out.println("Warten beendet :)");
		this.requestPermanentAddress();
	};

	private void requestPermanentAddress() {
		synchronized (lock1) {
		this.address = AddressSpaces.createTemporaryNodeAddress();
		System.out.println("Set own temporary address: " + this.address);
		this.sendATCommand("AT+ADDR=" + this.address);
		}
	};

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
}
