package de.htw.ai.hagen.TMS;

/**
 * Interprets any incoming Data from the Module
 * @author Hagen
 *
 */
public class HUHNPInterpreter {
	
	public static boolean gotExpectedAnswerFromModule = false;
	
	public static boolean parseIncomingData(String data) {
		
		if (data.contains("AT, OK ")){
			return true;
		}
		
		return false;
	}

}
