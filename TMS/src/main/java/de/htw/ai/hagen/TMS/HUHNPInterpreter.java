package de.htw.ai.hagen.TMS;

/**
 * Interprets any incoming Data from the Module
 * 
 * @author Hagen
 *
 */
public class HUHNPInterpreter {

	public static boolean gotExpectedAnswerFromModule = false;
	Object fakelock = new Object();

	public void parseIncomingData(String data) {

	// Module Communication
		// Print module communication to console except for AT,SENDING
		if (data.startsWith("AT,")&&!data.contains("AT,SENDING")) {
			System.out.print("[AT] " + data);
		}

		if (data.contains("AT,OK")) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					if (HUHNPController.isConfigured == true) {
						synchronized (HUHNPController.lock1) {
						HUHNPController.lock1.notifyAll();
						SimpleSender.preparedToSend = true;
						}
					} else {
						synchronized (HUHNPController.lock2) {
						HUHNPController.isConfigured = true;
						HUHNPController.lock2.notifyAll();
						}
					}
				}
				});
			t.start();
			}
		
		
		if (data.contains("AT,SENDED")) {
			synchronized (HUHNPController.lock2) {
				HUHNPController.lock2.notifyAll();
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
				if ((message.getDestinationAddress().equals(HUHNPController.address)
						|| message.getDestinationAddress().equals(HUHNPController.BROADCAST_ADDRESS))
								&& hasNotBeenForwardedBefore(message)) {

					/*----------------------------------------- COORDINATOR Role----------------------------------- */
					if (HUHNPController.isCoordinator) {

						if (data.contains("ALIV")) {
//							if (!hasNotBeenForwardedBefore(message)) {
								HUHNPController.sender.sendNetworkReset();
								coordinatorNRST();
								
//							}
						}

						//
						if (data.contains("CDIS")) {
							HUHNPController.sender.sendCoordinatorKeepAlive();
						}
						if (data.contains("ADDR")) {
							String address = AddressManager.generateNextFreePermanentAddress();
							HUHNPController.sender.sendAddress(message.getSourceAddress(), message.getMessageId(),
									address);
						}

						if (data.contains("AACK")) {
							HUHNPController.addressRegister.add(message.getSourceAddress());
						}

						if (data.contains("NRST")) {
							coordinatorNRST();

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
								HUHNPController.forwardingIsActive = true;
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
							HUHNPController.sender.setTemporaryAddress();
							HUHNPController.addressRegister = null;
							HUHNPController.forwardingIsActive = false;

						}

					}
				}
				// decide whether the node needs to forward the incoming message

				if (HUHNPController.forwardingIsActive && !(message.getDestinationAddress().equals(HUHNPController.address))) {
					if (isNotExpired(message) && hasNotBeenForwardedBefore(message) && !coordinatorIsMissing()) {
						HUHNPController.sender.sendMessage(incrementHopsByOne(message));
					}
				}
			} catch (Exception e) {
				System.out.println("[Incomplete Message: " + lastSender + "] " + incomingStringMessage);
				System.out.println(e.getMessage());
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
	
	private void coordinatorNRST() {
		HUHNPController.addressIsPermanent = false;
		HUHNPController.isCoordinator = false;
		HUHNPController.sender.setTemporaryAddress();
		HUHNPController.addressRegister = null;
		HUHNPController.forwardingIsActive = false;
	}
	
}
