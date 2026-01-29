package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchInitiator extends DepthFirstSearchProcess {

	@Override
	public void init() {
		// Initiator: send token to first available neighbor to start DFS
		Channel firstNeighbor = pickUnsentNeighbor();
		if (firstNeighbor != null) {
			send(new TokenMessage(), firstNeighbor);
			markSentTo(firstNeighbor);
		} else {
			// No neighbors, become passive immediately
			done();
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// Check message type
		if (!(m instanceof TokenMessage)) {
			throw new IllegalReceiveException();
		}

		// Check if already passive
		if (isPassive()) {
			throw new IllegalReceiveException();
		}

		// Mark that we received from this channel
		markReceivedFrom(c);

		// DFS Rule for initiator: Try to return token to sender first (if we haven't sent there yet)
		String senderName = c.getSender().getName();
		Channel outToSender = findOutgoingTo(senderName);
		if (outToSender != null && !hasSentTo(outToSender)) {
			send(new TokenMessage(), outToSender);
			markSentTo(outToSender);
		} else {
			// If can't return to sender, forward to next unsent neighbor
			Channel nextNeighbor = pickUnsentNeighbor();
			if (nextNeighbor != null) {
				send(new TokenMessage(), nextNeighbor);
				markSentTo(nextNeighbor);
			}
		}

		// Check if we can finish (received from all neighbors)
		if (allNeighborsReceived()) {
			done();
		}
	}
}
