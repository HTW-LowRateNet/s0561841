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
				if (HUHNPController.isConfigured == true) {
					SimpleSender.preparedToSend = true;
				} else {
					HUHNPController.isConfigured = true;
				}
				HUHNPController.lock1.notifyAll();
			}
		}

		// incoming message
		if (data.startsWith("LR,")) {
			String lastSender = data.substring(3, 7);
			String incomingStringMessage = data.substring(11);
			try {
				HUHNPMessage message = HUHNPMessage.parseToHUHNPMessage(incomingStringMessage);
				System.out.println("[Received: " + lastSender + "] " + incomingStringMessage);

				// decide whether this node needs to handle the incoming input
				// if (data.contains(HUHNPController.address) ||
				// data.contains(HUHNPController.BROADCAST_ADDRESS)) {

				if (message.getDestinationAddress().equals(HUHNPController.address)
						|| message.getDestinationAddress().equals(HUHNPController.BROADCAST_ADDRESS)
								&& hasNotBeenForwardedBefore(message)) {

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
							HUHNPController.sender.sendAddress(message.getSourceAddress(), message.getMessageId(),
									address);
							HUHNPController.allNodesInNetwork.add(address);
						}

						if (data.contains("AACK")) {
							HUHNPController.allNodesInNetwork.add(message.getSourceAddress());
						}

						if (data.contains("NRST")) {
							HUHNPController.addressIsPermanent = false;
							HUHNPController.isCoordinator = false;
							HUHNPController.sender.setTemporaryAddress();
							HUHNPController.allNodesInNetwork = null;

						}
					}
					/*----------------------------------------- CLIENT Role ----------------------------------- */
					if (!HUHNPController.isCoordinator) {

						// see if this message is especially for this node
						if (data.contains(HUHNPController.address)) {

							// this needs to set the address to the payload
							if (data.contains("ADDR") && !HUHNPController.addressIsPermanent) {
								HUHNPController.sender.setPermanentAddress(message.getPayload());
								HUHNPController.sender.sendAAcknowledgement(message);
							}
						}

						/* Handle all messages broadcasted */
						if (data.contains("ALIV")) {
							HUHNPController.coordinatorIsPresent = true;
							HUHNPController.forwardingIsActive = true;
							if (!HUHNPController.addressIsPermanent) {
								HUHNPController.sender.requestAddress();
							}
						}

						//
						if (data.contains("NRST")) {
							HUHNPController.addressIsPermanent = false;
							HUHNPController.sender.setTemporaryAddress();
							HUHNPController.allNodesInNetwork = null;

						}

					}
				}
				// decide whether the node needs to forward the incoming message
				// if (!data.contains(HUHNPController.address) ||
				// data.contains(HUHNPController.BROADCAST_ADDRESS)) {
				if (!(message.getDestinationAddress().equals(HUHNPController.address))) {
					if (isNotExpired(message) && hasNotBeenForwardedBefore(message) && !coordinatorIsMissing()) {
						HUHNPController.sender.sendMessage(incrementHopsByOne(message));
					}
				}
			} catch (Exception e) {
				System.out.println("[Incomplete Message: " + lastSender + "] " + incomingStringMessage);
			}
		}

	}

	private HUHNPMessage incrementHopsByOne(HUHNPMessage message) {
		Integer hopsPlusOne = Integer.parseInt(message.getCurrentHops()) + 1;
		String newHops = ((hopsPlusOne < 10) ? "0" : "") + hopsPlusOne;
		HUHNPMessage newMessage = message;
		newMessage.setCurrentHops(newHops);
		return newMessage;

	}

	private boolean hasNotBeenForwardedBefore(HUHNPMessage message) {
		return HUHNPController.forwardedMessageBuffer.contains(message) ? false : true;
	}

	private boolean isNotExpired(HUHNPMessage message) {
		return (message.getTimeToLive().compareTo(message.getCurrentHops()) > 0) ? true : false;
	}

	private boolean coordinatorIsMissing() {
		return (!HUHNPController.coordinatorIsPresent && !HUHNPController.isCoordinator) ? true : false;
	}
}
