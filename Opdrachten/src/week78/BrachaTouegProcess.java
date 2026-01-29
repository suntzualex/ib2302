package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import java.util.Set;
import java.util.HashSet;

public class BrachaTouegProcess extends DeadlockDetectionProcess {

	protected boolean notified = false;
	protected Channel firstNotifier = null;
	protected Set<Channel> notifiedChannels = new HashSet<>();
	protected Set<Channel> doneReceived = new HashSet<>();
	protected Set<Channel> grantedChannels = new HashSet<>();
	protected Set<Channel> ackReceived = new HashSet<>();
	protected Channel waitingForAcksFrom = null;
	protected Set<Channel> pendingAcks = new HashSet<>();
	protected boolean doneSentToFirstNotifier = false;

	@Override
	public void init() {
		// TODO
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// TODO
	}

	protected void handleNotify(Channel c) throws IllegalReceiveException {
		if (!notified) {
			// First notify received
			notified = true;
			firstNotifier = getOutgoingChannelTo(c.getSender());

			// Send notify to all outRequests
			for (Channel out : outRequests) {
				send(new NotifyMessage(), out);
				notifiedChannels.add(out);
			}

			// If no requests, send grants and wait for acks
			if (requests == 0) {
				for (Channel in : inRequests) {
					// inRequests contains incoming channels, send through outgoing channel to sender
					Channel outChannel = getOutgoingChannelTo(in.getSender());
					if (outChannel != null) {
						send(new GrantMessage(), outChannel);
						pendingAcks.add(outChannel);
					}
				}
				// If no pending acks and no notified channels, send done immediately
				if (pendingAcks.isEmpty() && notifiedChannels.isEmpty() && firstNotifier != null) {
					send(new DoneMessage(), firstNotifier);
				}
			}
		} else {
			// Already notified, send done back
			Channel outChannel = getOutgoingChannelTo(c.getSender());
			if (outChannel != null) {
				send(new DoneMessage(), outChannel);
			}
		}
	}

	protected void handleGrant(Channel c) throws IllegalReceiveException {
		if (grantedChannels.contains(c)) {
			throw new IllegalReceiveException();
		}
		grantedChannels.add(c);
		requests--;

		Channel outChannel = getOutgoingChannelTo(c.getSender());

		if (requests == 0) {
			// Send grants to all inRequests and wait for acks
			sendGrantsAndWaitForAcks(c);
		} else {
			// Just send ack
			if (outChannel != null) {
				send(new AckMessage(), outChannel);
			}
		}
	}

	protected void handleAck(Channel c) throws IllegalReceiveException {
		if (!ackReceived.contains(c)) {
			ackReceived.add(c);
			pendingAcks.remove(c);

			// Check if all acks received
			if (pendingAcks.isEmpty() && waitingForAcksFrom != null) {
				send(new AckMessage(), waitingForAcksFrom);
				waitingForAcksFrom = null;
				checkDone();
			}
		} else {
			throw new IllegalReceiveException();
		}
	}

	protected void handleDone(Channel c) throws IllegalReceiveException {
		if (!notifiedChannels.contains(c)) {
			throw new IllegalReceiveException();
		}
		if (doneReceived.contains(c)) {
			throw new IllegalReceiveException();
		}

		doneReceived.add(c);
		checkDone();
	}

	protected void sendGrantsAndWaitForAcks(Channel granter) {
		waitingForAcksFrom = getOutgoingChannelTo(granter.getSender());
		pendingAcks.clear();

		for (Channel in : inRequests) {
			// inRequests contains incoming channels, send through outgoing channel to sender
			Channel outChannel = getOutgoingChannelTo(in.getSender());
			if (outChannel != null) {
				send(new GrantMessage(), outChannel);
				pendingAcks.add(outChannel);
			}
		}

		// If no pending acks, send ack immediately
		if (pendingAcks.isEmpty() && waitingForAcksFrom != null) {
			send(new AckMessage(), waitingForAcksFrom);
			waitingForAcksFrom = null;
			checkDone();
		}
	}

	protected void checkDone() {
		// Check if all done messages received and all acks received
		if (!notified || firstNotifier == null || doneSentToFirstNotifier) {
			return;
		}

		// Check if we received DONE from all channels in notifiedChannels
		boolean allDone = true;
		for (Channel notifiedChannel : notifiedChannels) {
			// Check if we received DONE through the corresponding incoming channel
			boolean foundDone = false;
			for (Channel doneChannel : doneReceived) {
				if (doneChannel.getSender().equals(notifiedChannel.getReceiver())) {
					foundDone = true;
					break;
				}
			}
			if (!foundDone) {
				allDone = false;
				break;
			}
		}

		if (allDone && pendingAcks.isEmpty() && waitingForAcksFrom == null) {
			send(new DoneMessage(), firstNotifier);
			doneSentToFirstNotifier = true;
		}
	}

	protected Channel getOutgoingChannelTo(framework.Process p) {
		for (Channel c : getOutgoing()) {
			if (c.getReceiver().equals(p)) {
				return c;
			}
		}
		return null;
	}
}
