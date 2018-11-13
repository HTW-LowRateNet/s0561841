package de.htw.ai.hagen.TMS;

import java.util.Random;

public class AddressSpaces {
	
	public static int coordinatorAddress = 0x0000;
	public static int viceCoordinatorAddress = 0x0010;
	
	private static int temporaryAddressesUpperBound = 0x00FF;
	private static int temporaryAddressesLowerBound = 0x0011;
	
	private static int temporaryCoordinatorAddressesUpperBound = 0x000F;
	private static int temporaryCoordinatorAddressesLowerBound = 0x0001;
	
	
	/**
	 * this method is used by the Node on startup. It is necessary to request a permanent address.
	 * @return
	 */
	public static String createTemporaryNodeAddress() {
		Random rand = new Random();
		int myRandomNumber = rand.nextInt(temporaryAddressesUpperBound-temporaryAddressesLowerBound) + temporaryAddressesLowerBound; // Generates a random number between 0011 and 00FF (00EE+0011)
		System.out.printf("%x\n",myRandomNumber); // Prints it in hex, such as "0x14"
		// or....
		return Integer.toHexString(myRandomNumber).toUpperCase(); // Random hex number in result
	}
	
	/**
	 * This method is used to negotiate between Coordinators
	 * @return new temporary address
	 */
	public static String createTemporaryCoordinatorAddress() {
		Random rand = new Random();
		int myRandomNumber = rand.nextInt(temporaryCoordinatorAddressesUpperBound-temporaryCoordinatorAddressesLowerBound) + temporaryCoordinatorAddressesLowerBound; // Generates a random number between 0001 and 000F (00EE+0011)
		System.out.printf("%x\n",myRandomNumber); // Prints it in hex, such as "0x14"
		// or....
		return Integer.toHexString(myRandomNumber).toUpperCase(); // Random hex number in result
	}
}
