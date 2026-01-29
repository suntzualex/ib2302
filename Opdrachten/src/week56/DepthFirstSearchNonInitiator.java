package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchNonInitiator extends DepthFirstSearchProcess {

	@Override
	public void init() {
		// Non-initiator: do nothing until token received
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

		// Set parent if not set (first token received)
		if (getParent() == null) {
			setParent(c);
		}

		// DFS Rule for non-initiator: Try to return token to sender first (if we haven't sent there yet)
		// BUT avoid sending to parent unless it's the only option (handled by pickUnsentNeighbor)
		String senderName = c.getSender().getName();
		Channel outToSender = findOutgoingTo(senderName);

		// Check if sender is parent using same logic as pickUnsentNeighbor
		boolean senderIsParent = (getParent() != null &&
			senderName.equals(getParent().getSender().getName()));

		if (outToSender != null && !hasSentTo(outToSender) && !senderIsParent) {
			// Return to non-parent sender
			send(new TokenMessage(), outToSender);
			markSentTo(outToSender);
		} else {
			// If can't return to sender (or sender is parent), use pickUnsentNeighbor
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
