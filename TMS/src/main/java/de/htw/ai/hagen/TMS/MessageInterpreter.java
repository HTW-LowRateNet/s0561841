package de.htw.ai.hagen.TMS;

/**
 * Interprets any incoming Data from the Module
 * 
 * @author Hagen
 *
 */
public class MessageInterpreter {

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
					if (Controller.isConfigured == true) {
						synchronized (Controller.lock1) {
						Controller.lock1.notifyAll();
						Sender.preparedToSend = true;
						}
					} else {
						synchronized (Controller.lock2) {
						Controller.isConfigured = true;
						Controller.lock2.notifyAll();
						}
					}
				}
				});
			t.start();
			}
		
		
		if (data.contains("AT,SENDED")) {
			synchronized (Controller.lock2) {
				Controller.lock2.notifyAll();
			}
		}

		// incoming message
		if (data.startsWith("LR,")) {
			String lastSender = data.substring(3, 7);
			String incomingStringMessage = data.substring(11);
			try {
				Message message = Message.parseToHUHNPMessage(incomingStringMessage);
				System.out.println("[Received: " + lastSender + "] " + incomingStringMessage);

				// decide whether this node needs to handle the incoming input
				if ((message.getDestinationAddress().equals(Controller.address)
						|| message.getDestinationAddress().equals(Controller.BROADCAST_ADDRESS))
								&& hasNotBeenForwardedBefore(message)) {

					/*----------------------------------------- COORDINATOR Role----------------------------------- */
					if (Controller.isCoordinator) {

						if (data.contains("ALIV")) {
								Controller.sender.sendNetworkReset();
								coordinatorNRST();
						}

						if (data.contains("CDIS")) {
							Controller.sender.sendCoordinatorKeepAlive();
						}
						if (data.contains("ADDR")) {
							String address = AddressManager.generateNextFreePermanentAddress();
							Controller.sender.sendAddress(message.getSourceAddress(), message.getMessageId(),
									address);
						}

						if (data.contains("AACK")) {
							Controller.addressRegister.add(message.getSourceAddress());
						}

						if (data.contains("NRST")) {
							coordinatorNRST();
						}
					}
					
					/*----------------------------------------- CLIENT Role ----------------------------------- */
					if (!Controller.isCoordinator) {

						// see if this message is especially for this node
						if (data.contains(Controller.address)) {

							// this needs to set the address to the payload
							if (data.contains("ADDR") && !Controller.addressIsPermanent) {
								Controller.sender.setPermanentAddress(message.getPayload());
								Controller.sender.sendAAcknowledgement(message);
								Controller.forwardingIsActive = true;
							}
						}

						/* Handle all messages broadcasted */
						if (data.contains("ALIV")) {
							Controller.coordinatorIsPresent = true;
							if (!Controller.addressIsPermanent) {
								Controller.sender.requestAddress();
							}
						}

						//
						if (data.contains("NRST")) {
							Controller.addressIsPermanent = false;
							Controller.sender.setTemporaryAddress();
							Controller.addressRegister = null;
							Controller.forwardingIsActive = false;

						}

					}
				}
				// decide whether the node needs to forward the incoming message

				if (Controller.forwardingIsActive && !(message.getDestinationAddress().equals(Controller.address))) {
					if (isNotExpired(message) && hasNotBeenForwardedBefore(message) && !coordinatorIsMissing()) {
						Controller.sender.sendMessage(incrementHopsByOne(message));
					}
				}
			} catch (Exception e) {
				System.out.println("[Incomplete Message: " + lastSender + "] " + incomingStringMessage);
				System.out.println(e.getMessage());
			}
		}

	}

	private Message incrementHopsByOne(Message message) {
		Integer hopsPlusOne = Integer.parseInt(message.getCurrentHops()) + 1;
		String newHops = ((hopsPlusOne < 10) ? "0" : "") + hopsPlusOne;
		Message newMessage = message;
		newMessage.setCurrentHops(newHops);
		return newMessage;

	}

	private boolean hasNotBeenForwardedBefore(Message message) {
		return Controller.forwardedMessageBuffer.contains(message) ? false : true;
	}

	private boolean isNotExpired(Message message) {
		return (message.getTimeToLive().compareTo(message.getCurrentHops()) > 0) ? true : false;
	}

	private boolean coordinatorIsMissing() {
		return (!Controller.coordinatorIsPresent && !Controller.isCoordinator) ? true : false;
	}
	
	private void coordinatorNRST() {
		Controller.addressIsPermanent = false;
		Controller.isCoordinator = false;
		Controller.sender.setTemporaryAddress();
		Controller.addressRegister = null;
		Controller.forwardingIsActive = false;
		AddressManager.nextFreeAddress = AddressManager.permanentAddressesLowerBound;
	}
	
}
