package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

import java.util.*;

public abstract class DepthFirstSearchExtraControlProcess extends WaveProcess {

	protected final Set<Channel> sentInfo = new HashSet<>();
	protected final Set<Channel> receivedInfo = new HashSet<>();
	protected final Set<Channel> sentToken = new HashSet<>();
	protected final Set<Channel> pendingAcks = new HashSet<>();
	protected Channel parent = null;

	protected void markSentInfo(Channel c) {
		sentInfo.add(c);
	}

	protected void markReceivedInfo(Channel c) {
		receivedInfo.add(c);
	}

	protected void markSentToken(Channel c) {
		sentToken.add(c);
	}

	protected boolean hasSentToken(Channel c) {
		return sentToken.contains(c);
	}

	protected boolean hasReceivedInfo(Channel c) {
		return receivedInfo.contains(c);
	}

	protected void setParent(Channel c) {
		parent = c;
	}

	protected Channel getParent() {
		return parent;
	}

	protected void addPendingAck(Channel c) {
		pendingAcks.add(c);
	}

	protected void removePendingAck(Channel c) {
		pendingAcks.remove(c);
	}

	protected boolean allAcksReceived() {
		return pendingAcks.isEmpty();
	}

	// Pick next child according to rules:
	// 1. Never send token twice
	// 2. Never send token to processes that sent INFO to us
	// 3. Never return parent as a child (parent is for returning, not exploring)
	protected Channel pickChild() {
		for (Channel c : getOutgoing()) {
			// Skip if already sent token
			if (hasSentToken(c)) continue;

			// Skip if received INFO from this process (can't send token to them)
			if (hasReceivedInfo(c)) continue;

			// Skip if this is parent (parent is not a child)
			boolean isParent = (parent != null && c.getReceiver().getName().equals(parent.getSender().getName()));
			if (isParent) continue;

			// Found a valid child
			return c;
		}

		// No valid child found
		return null;
	}

	protected Channel findOutgoingTo(String processName) {
		for (Channel out : getOutgoing()) {
			if (out.getReceiver().getName().equals(processName)) {
				return out;
			}
		}
		return null;
	}

	@Override
	public void init() {
		// Default implementation
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// Default implementation
		throw new IllegalReceiveException();
	}
}
