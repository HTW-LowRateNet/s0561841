package de.htw.ai.hagen.TMS;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.*;

import java.time.LocalDateTime;
import java.util.HashMap;

public class MessageBuffer {

	public static final int INTERVALL_IN_MINUTES = 1;

	public Map<LocalDateTime, Message> forwardedMessages = new HashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public MessageBuffer() {
		scheduler.scheduleAtFixedRate(cleanUpTask, INTERVALL_IN_MINUTES, INTERVALL_IN_MINUTES, MINUTES);
	}

	public boolean contains(Message message) {
		if (!forwardedMessages.isEmpty()) {
			for (Entry<LocalDateTime, Message> entry : forwardedMessages.entrySet()) {
				if (entry.getValue().getMessageId().equals(message.getMessageId())
						&& entry.getValue().getDestinationAddress().equals(message.getDestinationAddress())
						&& entry.getValue().getSourceAddress().equals(message.getSourceAddress())) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void addForwardedMessage(Message message) {
		forwardedMessages.put(LocalDateTime.now(), message);
	}

	private final Runnable cleanUpTask = new Runnable() {
		public void run() {
			if (!forwardedMessages.isEmpty()) {
				int counter = 0;
				for (Entry<LocalDateTime, Message> entry : forwardedMessages.entrySet()) {
					if (entry.getKey().plusMinutes(INTERVALL_IN_MINUTES).isBefore(LocalDateTime.now())) {
						counter++;
						forwardedMessages.remove(entry.getKey());
					}
				}
			}
		}
	};

}
