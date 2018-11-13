package de.htw.ai.hagen.TMS;

public enum MessageCode {

	/** Code for neighbor discovery. */
	DISC("DISC"),
	/** Code for discovering the current Coordinator of the network */
	KDIS("KDIS"),
	/** Code for Setting the Node's address */
	ADDR("ADDR"),
	/** Code for a simple message */
	MSSG("MSSG"),
	/** Code for self-polling to detect address collision */
	POLL("POLL");

	protected String code;

	// Constructor
	MessageCode(String s) {
		this.code = s;
	};

	//
	public String code() {
		return code;
	}

}
