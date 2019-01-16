package de.htw.ai.hagen.TMS;

public enum MessageCode {


	/** Code for discovering the current Coordinator of the network */
	CDIS("CDIS"),
	/** Code for Setting the Node's address */
	ADDR("ADDR"),
	/** Code for a simple message */
	MSSG("MSSG"),
	/** Code for letting network know that this node is the coordinator */
	ALIV("ALIV"), 
	/**	Code for letting the network know that it needs to be reset */
	NRST("NRST"), 
	/**	Code to acknowledge an ADDR Response from the coordinator */
	AACK("AACK");


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
