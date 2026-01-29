package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

import java.util.*;

public abstract class LaiYangProcess extends SnapshotProcess {

	private final Map<Channel, Boolean> markerReceived = new HashMap<>();
	private final Map<Channel, List<Message>> channelStates = new HashMap<>();
	private final Set<Channel> recordingChannels = new HashSet<>();
	private final Map<Channel, Integer> controlNumbers = new HashMap<>();
	private final Map<Channel, Integer> expectedFalseMessages = new HashMap<>();

	@Override
	public void init() {
		// Default: do nothing, subclasses override
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		System.out.println("[DEBUG] receive: " + m + " on " + c + ", hasStarted=" + hasStarted() + ", hasFinished=" + hasFinished());
		if (hasFinished()) throw new IllegalReceiveException();
		if (m == null || c == null) throw new IllegalReceiveException();
		if (m == Message.DUMMY) throw new IllegalReceiveException();

		if (m instanceof LaiYangControlMessage) {
			LaiYangControlMessage cm = (LaiYangControlMessage) m;
			if (markerReceived.getOrDefault(c, false)) throw new IllegalReceiveException();
			markerReceived.put(c, true);
			controlNumbers.put(c, cm.getN());

			// Count how many false messages were already recorded on this channel
			int alreadyRecorded = 0;
			if (channelStates.containsKey(c)) {
				for (Message msg : channelStates.get(c)) {
					if (msg instanceof LaiYangBasicMessage && !((LaiYangBasicMessage) msg).getTag()) {
						alreadyRecorded++;
					}
				}
			}

			// Set expected false messages accounting for already recorded ones
			int stillExpected = Math.max(0, cm.getN() - alreadyRecorded);
			expectedFalseMessages.put(c, stillExpected);
			System.out.println("[DEBUG] ControlMessage received on " + c + ", expectedFalseMessages=" + cm.getN() + ", alreadyRecorded=" + alreadyRecorded + ", stillExpected=" + stillExpected);

			if (!hasStarted()) {
				startSnapshot();
				recordLocalState();
				sendMarkerMessages();
				for (Channel in : getIncoming()) {
					if (!in.equals(c)) startRecording(in);
				}
			}

			// Only stop recording if all expected false messages have been received
			if (stillExpected <= 0) {
				stopRecording(c);
				System.out.println("[DEBUG] Stopped recording on " + c + " - all expected messages already received");
			} else {
				System.out.println("[DEBUG] Continuing recording on " + c + " - still expecting " + stillExpected + " false messages");
			}

			checkFinish();
			return;
		}

		if (m instanceof LaiYangBasicMessage) {
			LaiYangBasicMessage bm = (LaiYangBasicMessage) m;
			System.out.println("[DEBUG] BasicMessage received on " + c + ", tag=" + bm.getTag());
			if (!hasStarted() && bm.getTag()) {
				startSnapshot();
				recordLocalState();
				sendMarkerMessages();
				for (Channel in : getIncoming()) {
					if (!in.equals(c)) startRecording(in);
				}
			}

			// If recording on this channel and this is a false-tagged message
			if (recordingChannels.contains(c) && !bm.getTag()) {
				recordMessage(c, bm);
				// Decrement expected false messages for this channel
				if (expectedFalseMessages.containsKey(c)) {
					int left = expectedFalseMessages.get(c) - 1;
					expectedFalseMessages.put(c, left);
					System.out.println("[DEBUG] Decremented expectedFalseMessages for " + c + ": now " + left);
					if (left <= 0) {
						stopRecording(c);
						System.out.println("[DEBUG] Stopped recording on " + c + " - all expected messages received");
					}
				}
				checkFinish();
			} else if (!recordingChannels.contains(c) && !bm.getTag()) {
				// If marker has been received and we're not recording, reject false messages
				System.out.println("[DEBUG] IllegalReceiveException: tag=false after recording stopped on " + c);
				throw new IllegalReceiveException();
			}
			return;
		}

		System.out.println("[DEBUG] IllegalReceiveException: unknown message type");
		throw new IllegalReceiveException();
	}

	protected void recordLocalState() {
		// Optionally override for debug
	}

	protected void sendMarkerMessages() {
		for (Channel out : getOutgoing()) {
			send(new LaiYangControlMessage(0), out);
		}
	}

	protected void startRecording(Channel c) {
		recordingChannels.add(c);
		channelStates.putIfAbsent(c, new ArrayList<>());
	}

	protected void stopRecording(Channel c) {
		recordingChannels.remove(c);
	}

	protected void recordMessage(Channel c, Message m) {
		channelStates.get(c).add(m);
	}

	private void checkFinish() {
		System.out.println("[DEBUG] checkFinish: started=" + hasStarted() + ", finished=" + hasFinished());
		for (Channel in : getIncoming()) {
			System.out.println("[DEBUG] Channel " + in + ": markerReceived=" + markerReceived.getOrDefault(in, false) + ", recording=" + recordingChannels.contains(in) + ", expectedFalseMessages=" + expectedFalseMessages.getOrDefault(in, 0));
		}
		if (!hasStarted() || hasFinished()) return;

		// All incoming channels must have received a marker
		for (Channel in : getIncoming()) {
			if (!markerReceived.getOrDefault(in, false)) {
				System.out.println("[DEBUG] Not finished: marker not received on " + in);
				return;
			}
		}

		// All expected basic messages must have been received for each channel
		for (Channel in : getIncoming()) {
			if (expectedFalseMessages.containsKey(in) && expectedFalseMessages.get(in) > 0) {
				System.out.println("[DEBUG] Not finished: still expecting " + expectedFalseMessages.get(in) + " false messages on " + in);
				return;
			}
		}

		System.out.println("[DEBUG] finishSnapshot() called - all conditions met");
		finishSnapshot();
	}
}
