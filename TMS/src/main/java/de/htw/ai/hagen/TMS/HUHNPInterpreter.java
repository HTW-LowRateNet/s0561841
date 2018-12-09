package de.htw.ai.hagen.TMS;

/**
 * Interprets any incoming Data from the Module
 * 
 * @author Hagen
 *
 */
public class HUHNPInterpreter {

	public static boolean gotExpectedAnswerFromModule = false;

	public void parseIncomingData(String data) {

		// Module Communication
		if (data.startsWith("AT,")) {
			System.out.println("[AT] " + data);
		}

		if (data.contains("AT,OK")) {
			synchronized (HUHNPController.lock1) {
				SimpleSender.preparedToSend = true;
				HUHNPController.lock1.notifyAll();
			}
		}

		// incoming message
		if (data.startsWith("LR,")) {
			String lastSender = data.substring(4, 7);
			String incomingStringMessage = data.substring(11);
			HUHNPMessage message = HUHNPMessage.parseToHUHNPMessage(incomingStringMessage);
			System.out.println("[Received: " + lastSender + "] " + incomingStringMessage);

			// decide whether this node needs to handle or forward the incoming input
			if (data.contains(HUHNPController.address) || data.contains(HUHNPController.BROADCAST_ADDRESS)) {

				/*----------------------------------------- COORDINATOR Role----------------------------------- */
				if (HUHNPController.isCoordinator) {

					if (data.contains("ALIV")) {
						if (HUHNPController.isCoordinator == true) {
							HUHNPController.sender.sendNetworkReset();
						}
					}

					//
					if (data.contains("CDIS")) {
						HUHNPController.sender.sendCoordinatorKeepAlive();
					}
					if (data.contains("ADDR")) {
						String address = AddressManager.generateNextFreePermanentAddress();
						HUHNPController.sender.sendAddress(message.getSourceAddress(), message.getMessageId(), address);
						HUHNPController.allNodesInNetwork.add(address);
						

						// send network reset if this node is coordinator and receives another
						// coordinators message
						if (HUHNPController.isCoordinator == true) {
							HUHNPController.sender.sendNetworkReset();
						}
					}
				}
				/*----------------------------------------- CLIENT Role ----------------------------------- */
				if (!HUHNPController.isCoordinator) {

					// see if this message is especially for this node
					if (data.contains(HUHNPController.address)) {

						// this needs to set the address to the payload
						if (data.contains("ADDR") && !HUHNPController.addressIsPermanent) {
							HUHNPController.sender.setPermanentAddress(message.getPayload());
							
						}
					}
					
					/* Handle all messages broadcasted */
					if (data.contains("ALIV")) {
						HUHNPController.coordinatorIsPresent = true;
						if (!HUHNPController.addressIsPermanent) {
							HUHNPController.sender.requestAddress();
						}
					}

					//
					if (data.contains("NRST")) {
						HUHNPController.addressIsPermanent = false;
						HUHNPController.coordinatorIsPresent = false;
						HUHNPController.sender.setTemporaryAddress();
						HUHNPController.neighbors = null;
						HUHNPController.allNodesInNetwork = null;

					}

				}
			}
		}

	}
}
