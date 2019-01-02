package de.htw.ai.hagen.TMS;

import java.io.IOException;

/**
 * This example code demonstrates how to perform serial communications using the
 * Raspberry Pi.
 *
 * @author Hagen Wittlich
 * 
 *  Based on J4Pi example by Robert Savage
 */
public class App {
	
	public static void main(String args[])  {

		HUHNPController controller = new HUHNPController();
		
		try {
			controller.runHUHNPController(args);
		} catch (InterruptedException | IOException e) {
			System.out.println("Something went wrong: ");
			e.printStackTrace();
		}
	}
		
}
