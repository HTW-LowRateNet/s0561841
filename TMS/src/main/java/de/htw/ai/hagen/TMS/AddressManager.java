package de.htw.ai.hagen.TMS;

import java.util.Random;

public class AddressManager {
	
	public static int coordinatorAddress = 0x0000;
		
	private static int temporaryAddressesUpperBound = 0x0FFF;
	private static int temporaryAddressesLowerBound = 0x0011;
	
	public static int permanentAddressesUpperBound = 0xFFFE;
	public static int permanentAddressesLowerBound = 0x1000;
	
	public static Integer nextFreeAddress = permanentAddressesLowerBound; 
		
	/**
	 * this method is used by the Node on startup. It is necessary to request a permanent address.
	 * @return
	 */
	public static String createTemporaryNodeAddress() {
		Random rand = new Random();
		int myRandomNumber = rand.nextInt(temporaryAddressesUpperBound-temporaryAddressesLowerBound) + temporaryAddressesLowerBound; // Generates a random number between 0011 and 00FF (00EE+0011)
		return String.format("%1$04X", myRandomNumber); // Random hex number in result
	}
	
	
	public static String generateNextFreePermanentAddress() {
		Integer newAddress = nextFreeAddress;
		nextFreeAddress++;
		return String.format("%1$04X",newAddress);
	}


}
