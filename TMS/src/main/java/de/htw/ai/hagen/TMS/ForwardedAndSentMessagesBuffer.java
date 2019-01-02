package de.htw.ai.hagen.TMS;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.*;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ForwardedAndSentMessagesBuffer {

	public static final int INTERVALL_IN_MINUTES = 1;

	public Map<LocalDateTime, HUHNPMessage> forwardedMessages = new HashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ForwardedAndSentMessagesBuffer() {
		scheduler.scheduleAtFixedRate(cleanUpTask, INTERVALL_IN_MINUTES, INTERVALL_IN_MINUTES, MINUTES);
	}

	public boolean contains(HUHNPMessage message) {
		if (!forwardedMessages.isEmpty()) {
			for (Entry<LocalDateTime, HUHNPMessage> entry : forwardedMessages.entrySet()) {
				if (entry.getValue().getMessageId() == message.getMessageId()
						&& entry.getValue().getDestinationAddress() == message.getDestinationAddress()
						&& entry.getValue().getSourceAddress() == message.getSourceAddress()) {
					return true;
				}
			}
		}
		return false;
	}

	public void addForwardedMessage(HUHNPMessage message) {
		forwardedMessages.put(LocalDateTime.now(), message);
	}

	private final Runnable cleanUpTask = new Runnable() {
		public void run() {
			System.out.println("[Scheduled Task] starting cleanup");
			if (!forwardedMessages.isEmpty()) {
				int counter = 0;
				for (Entry<LocalDateTime, HUHNPMessage> entry : forwardedMessages.entrySet()) {
					if (entry.getKey().plusMinutes(INTERVALL_IN_MINUTES).isBefore(LocalDateTime.now())) {
						counter++;
						forwardedMessages.remove(entry.getKey());
					}
				}
				System.out.println("[Scheduled Task] Removed " + counter + " forwarded messages from buffer.");
			}
		}
	};

}
