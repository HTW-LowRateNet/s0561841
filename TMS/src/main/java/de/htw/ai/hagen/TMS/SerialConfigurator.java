package de.htw.ai.hagen.TMS;

import java.io.IOException;

import com.pi4j.io.gpio.exception.UnsupportedBoardType;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialPort;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

public class SerialConfigurator {
	private Serial serial;
	private Console console;
	
	public SerialConfigurator(Serial serial, Console console) {
		this.serial = serial;
		this.console = console;
	}
	
	protected void configureSerial(String[] args) {
		try {
			// create serial config object
			SerialConfig config = new SerialConfig();

			/*
			 * set default serial settings (device, baud rate, flow control, etc) by
			 * default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO
			 * header) NOTE: this utility method will determine the default serial port for
			 * the detected platform and board/model. For all Raspberry Pi models except the
			 * 3B, it will return "/dev/ttyAMA0". For Raspberry Pi model 3B may return
			 * "/dev/ttyS0" or "/dev/ttyAMA0" depending on environment configuration.
			 */
			config.device(SerialPort.getDefaultPort()).baud(Baud._115200).dataBits(DataBits._8).parity(Parity.NONE)
					.stopBits(StopBits._1).flowControl(FlowControl.NONE);

			// parse optional command argument options to override the default serial
			// settings.
			if (args.length > 0) {
				config = CommandArgumentParser.getSerialConfig(config, args);
			}
			// display connection details
			console.box(" Connecting to: " + config.toString());
			// open the default serial device/port with the configuration settings
			serial.open(config);


		} catch (IOException | UnsupportedBoardType | InterruptedException ex) {
			console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
			return;
		}
	}
}
