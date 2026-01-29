package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegNonInitiator extends BrachaTouegProcess {

	@Override
	public void init() {
		// Non-initiator does nothing on init
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (m instanceof NotifyMessage) {
			handleNotify(c);
		} else if (m instanceof GrantMessage) {
			if (grantedChannels.contains(c)) {
				throw new IllegalReceiveException();
			}
			grantedChannels.add(c);
			requests--;

			Channel outChannel = getOutgoingChannelTo(c.getSender());

			if (requests == 0 && waitingForAcksFrom == null) {
				// Send grants to all inRequests and wait for acks
				waitingForAcksFrom = outChannel;
				pendingAcks.clear();
				for (Channel in : inRequests) {
					Channel outCh = getOutgoingChannelTo(in.getSender());
					if (outCh != null) {
						send(new GrantMessage(), outCh);
						pendingAcks.add(outCh);
					}
				}

				// If no pending acks, send ack immediately and check done
				if (pendingAcks.isEmpty() && waitingForAcksFrom != null) {
					send(new AckMessage(), waitingForAcksFrom);
					waitingForAcksFrom = null;
					checkDone();
				}
			} else {
				// Just send ack (either requests > 0 or already waiting for acks)
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

			// Check if all acks received
			if (pendingAcks.isEmpty()) {
				if (waitingForAcksFrom != null) {
					send(new AckMessage(), waitingForAcksFrom);
					waitingForAcksFrom = null;
				}
				checkDone();
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
			checkDone();
		} else {
			throw new IllegalReceiveException();
		}
	}
}

