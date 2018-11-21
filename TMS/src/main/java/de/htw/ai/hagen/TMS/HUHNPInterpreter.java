package de.htw.ai.hagen.TMS;

/**
 * Interprets any incoming Data from the Module
 * @author Hagen
 *
 */
public class HUHNPInterpreter {
	
	public static boolean gotExpectedAnswerFromModule = false;
	private static HUHNPSender sender;
	
	public HUHNPInterpreter(HUHNPSender sender) {
		HUHNPInterpreter.sender = sender;
	}
	
	public static void parseIncomingData(String data) {

		if (data.contains("AT,OK")){
			HUHNPSender.preparedToSend = true;
			synchronized (HUHNPController.lock1) {
				HUHNPController.lock1.notify();
			}
		}
		if (data.contains("ALIV")){
			HUHNPController.coordinatorIsPresent = true;
		}
		if (data.contains("KDIS") && HUHNPController.isCoordinator ==true ){
			sender.sendCoordinatorKeepAlive();
		}
	}

}
