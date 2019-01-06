package de.htw.ai.hagen.TMS;

import java.io.IOException;
import java.util.Set;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.util.Console;

public class HUHNPController {
	// Structural variables
	final static Console console = new Console();
	public static Boolean lock1 = true;
	public static Boolean lock2 = true;
	public static Boolean moduleInUse = false;
	final static Serial serial = SerialFactory.createInstance(); // create an instance of the serial communications
	final static SimpleSender sender = new SimpleSender(serial);// create an instance of the Sender class
	final static HUHNPInterpreter interpreter = new HUHNPInterpreter();
	public static ForwardedAndSentMessagesBuffer forwardedMessageBuffer = new ForwardedAndSentMessagesBuffer();
	final static SerialConfigurator configurator = new SerialConfigurator(serial, console);

	// business logic variables
	public static String address = ""; // Current address of this node
	public final static String BROADCAST_ADDRESS = "FFFF";
	static boolean isConfigured = false;
	static boolean isCoordinator = false; // Is this node the coordinator in the current network?
	static boolean addressIsPermanent = false; // has this node received a permanent address yet?
	static boolean coordinatorIsPresent = false;
	static boolean forwardingIsActive = false;
	static Set<String> addressRegister = null; // If this node is coordinator, it should keep a record of all nodes currently
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

		// configure module
		configurator.configureSerial(args);

		// create and register the serial data listener
		Thread serialListener = new Thread(new SerialInputListener(serial, interpreter));
		Thread userInputListener = new Thread(new UserInputListener(sender));
		serialListener.start();
		userInputListener.start();

		// start initial congfiguration of module and set own temporary address
		sender.configureModule();

		// continuous loop to keep the program running until the user terminates the
		// program
		while (console.isRunning()) {

			if (isConfigured) {

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
						forwardingIsActive = false;
						
						for (int i = 0; i <= 6; i++) {
							sender.discoverPANCoordinator();
							Thread.sleep(2000);
							if (coordinatorIsPresent) {
								break;
							}
						}
						if (!coordinatorIsPresent) {
							this.imTheCaptainNow();
						}
					}
				}
			}
		}
	}

	/**
	 * method to make this node the coordinator of the network
	 */
	protected void imTheCaptainNow() {
		address = sender.setPermanentAddress("0000");
		HUHNPController.isCoordinator = true;
	}

}
