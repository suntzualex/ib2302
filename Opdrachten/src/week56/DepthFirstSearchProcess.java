package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import java.util.HashSet;
import java.util.Set;

public abstract class DepthFirstSearchProcess extends WaveProcess {

	// Track which channels we have sent a token to
	private final Set<Channel> sentTo = new HashSet<>();
	// Track how many tokens we've received from each incoming channel
	private final Set<Channel> receivedFrom = new HashSet<>();
	// The parent channel (first channel from which we received a token)
	private Channel parent = null;

	protected boolean hasReceivedFrom(Channel c) {
		return receivedFrom.contains(c);
	}

	protected void markReceivedFrom(Channel c) {
		receivedFrom.add(c);
	}

	protected boolean hasSentTo(Channel c) {
		return sentTo.contains(c);
	}

	protected void markSentTo(Channel c) {
		sentTo.add(c);
	}

	protected Channel getParent() {
		return parent;
	}

	protected void setParent(Channel c) {
		parent = c;
	}

	protected boolean allNeighborsReceived() {
		return receivedFrom.size() == getIncoming().size();
	}

	// Pick an unsent neighbor, avoiding parent unless it's the only option
	protected Channel pickUnsentNeighbor() {
		// First try to find a non-parent unsent neighbor
		for (Channel out : getOutgoing()) {
			if (!hasSentTo(out)) {
				// Check if this channel goes to the parent process
				if (parent != null && out.getReceiver().getName().equals(parent.getSender().getName())) {
					continue; // Skip parent for now
				}
				return out; // Found non-parent unsent neighbor
			}
		}
		// If only parent is left unsent, return it
		for (Channel out : getOutgoing()) {
			if (!hasSentTo(out)) {
				return out;
			}
		}
		return null;
	}

	// Find outgoing channel to a specific process
	protected Channel findOutgoingTo(String processName) {
		for (Channel out : getOutgoing()) {
			if (out.getReceiver().getName().equals(processName)) {
				return out;
			}
		}
		return null;
	}
}
