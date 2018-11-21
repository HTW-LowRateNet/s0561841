package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.List;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.util.Console;

public class HUHNPController {
	// Structural variables
	final static Console console = new Console();
	public static Boolean lock1 = true;
	final static Serial serial = SerialFactory.createInstance(); // create an instance of the serial communications
	final static HUHNPSender sender = new HUHNPSender(serial);// create an instance of the Sender class
	final static SerialConfigurator configurator = new SerialConfigurator(serial, console);

	// business logic variables
	String address; // Current address of this node
	static boolean isCoordinator = false; // Is this node the coordinator in the current network?
	static boolean coordinatorIsPresent = false;
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

		console.title("<-- HUHN-P Project -->", "Technik Mobiler Systeme");

		// allow for user to exit program using CTRL-C
		console.promptForExit();
		
		//configure module
		configurator.configureSerial(args);
		
		// create and register the serial data listener
		Thread serialListener = new Thread(new SerialInputListener(serial));
		Thread userInputListener = new Thread(new UserInputListener(sender));
		serialListener.start();
		userInputListener.start();

		// start initial congfiguration of module and set own temporary address
		sender.configureModule();


		// continuous loop to keep the program running until the user terminates the
		// program
		while (console.isRunning()) {

			if (isCoordinator) {
				sender.sendCoordinatorKeepAlive();
				Thread.sleep(5000);
			} else {
				if (coordinatorIsPresent) {
					System.out.println("Coordinator is present.");
					coordinatorIsPresent = false;
					Thread.sleep(10000);
				} else {
					System.out.println("Missing Coordinator.");
					for (int i = 0; i <= 6; i++) {
						sender.discoverPANCoordinator();
						Thread.sleep(1000);
						if(coordinatorIsPresent) {break;}
						}
					if(!coordinatorIsPresent) {this.imTheCaptainNow();}
					
				}

			}

			try {

			} catch (IllegalStateException ex) {
				ex.printStackTrace();
			}

			// wait 1 second before continuing
			Thread.sleep(1000);
		}

	}

	protected void imTheCaptainNow() {
		this.address = sender.setPermanentAddress("0000");
		HUHNPController.isCoordinator = true;
	}

}
