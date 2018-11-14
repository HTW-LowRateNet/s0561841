package de.htw.ai.hagen.TMS;

import java.util.Scanner;

import com.pi4j.io.serial.Serial;

public class UserInputListener implements Runnable {

	private HUHNPController controller;

	public UserInputListener(HUHNPController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		while(true) {
		Scanner inputreader = new Scanner(System.in).useDelimiter(System.getProperty("line.separator"));
		while(inputreader.hasNext()) {
		String userinput = inputreader.next() ;
		System.out.println("User typed: " + userinput);
		controller.sendATCommand(userinput);
		}
		inputreader.close();
		}
	}

}
