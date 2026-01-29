package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegInitiator extends BrachaTouegProcess {

	private boolean resultPrinted = false;

	@Override
	public void init() {
		// Conceptually send notify to self
		notified = true;
		firstNotifier = null; // Initiator has no first notifier

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
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (m instanceof NotifyMessage) {
			// Initiator already notified, send done back
			Channel outChannel = getOutgoingChannelTo(c.getSender());
			if (outChannel != null) {
				send(new DoneMessage(), outChannel);
			}
		} else if (m instanceof GrantMessage) {
			if (grantedChannels.contains(c)) {
				throw new IllegalReceiveException();
			}
			grantedChannels.add(c);
			requests--;

			Channel outChannel = getOutgoingChannelTo(c.getSender());

			if (requests == 0 && waitingForAcksFrom == null) {
				// Send grants to all inRequests and wait for acks before sending ack to granter
				waitingForAcksFrom = outChannel;
				for (Channel in : inRequests) {
					Channel outCh = getOutgoingChannelTo(in.getSender());
					if (outCh != null) {
						send(new GrantMessage(), outCh);
						pendingAcks.add(outCh);
					}
				}

				// If no pending acks, send ack immediately
				if (pendingAcks.isEmpty() && waitingForAcksFrom != null) {
					send(new AckMessage(), waitingForAcksFrom);
					waitingForAcksFrom = null;
				}
			} else {
				// Send ack back immediately (either requests > 0 or already waiting for acks)
				if (outChannel != null) {
					send(new AckMessage(), outChannel);
				}
			}
		} else if (m instanceof AckMessage) {
			// Find the corresponding outgoing channel for this ACK
			Channel outChannel = getOutgoingChannelTo(c.getSender());
			if (outChannel == null || !pendingAcks.contains(outChannel) || ackReceived.contains(c)) {
				throw new IllegalReceiveException();
			}
			ackReceived.add(c);
			pendingAcks.remove(outChannel);

			// If all acks received and waiting for someone, send ack to them
			if (pendingAcks.isEmpty() && waitingForAcksFrom != null) {
				send(new AckMessage(), waitingForAcksFrom);
				waitingForAcksFrom = null;
			}

			// If all acks received and no outRequests, check if algorithm is complete
			if (!resultPrinted && pendingAcks.isEmpty() && notifiedChannels.isEmpty()) {
				// No outRequests means no DONEs to wait for, so check completion now
				boolean isFree = (requests == 0);
				print(String.valueOf(isFree));
				resultPrinted = true;
			}
		} else if (m instanceof DoneMessage) {
			// Find the corresponding outgoing channel for this DONE
			Channel outChannel = getOutgoingChannelTo(c.getSender());
			if (outChannel == null || !notifiedChannels.contains(outChannel)) {
				throw new IllegalReceiveException();
			}
			if (doneReceived.contains(c)) {
				throw new IllegalReceiveException();
			}
			doneReceived.add(c);

			// Check if algorithm is finished (all DONEs received from notified channels)
			// need to check if we received DONE from all channels in notifiedChannels
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

			if (allDone && !resultPrinted) {
				// Decide if there is a deadlock
				boolean isFree = (requests == 0);
				print(String.valueOf(isFree));
				resultPrinted = true;
			}
		} else {
			throw new IllegalReceiveException();
		}
	}
}

