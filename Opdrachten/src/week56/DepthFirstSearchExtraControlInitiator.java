package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlInitiator extends DepthFirstSearchExtraControlProcess {

	private Channel currentChild = null;

	@Override
	public void init() {
		if (getOutgoing().isEmpty()) {
			done();
			return;
		}

		// Pick first child
		currentChild = pickChild();
		if (currentChild != null) {
			// Send INFO to all neighbors except the chosen child
			for (Channel c : getOutgoing()) {
				if (!c.equals(currentChild)) {
					send(new InfoMessage(), c);
					markSentInfo(c);
					addPendingAck(c);
				}
			}

			// If no INFO sent (only one neighbor), send token immediately
			if (allAcksReceived()) {
				send(new TokenMessage(), currentChild);
				markSentToken(currentChild);
			}
		} else {
			done();
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (isPassive()) {
			throw new IllegalReceiveException();
		}

		if (m instanceof InfoMessage) {
			String senderName = c.getSender().getName();
			Channel outToSender = findOutgoingTo(senderName);
			if (outToSender != null) {
				markReceivedInfo(outToSender);
				send(new AckMessage(), outToSender);
			}
			return;
		}

		if (m instanceof AckMessage) {
			String senderName = c.getSender().getName();
			Channel outToSender = findOutgoingTo(senderName);
			if (outToSender == null || !pendingAcks.contains(outToSender)) {
				throw new IllegalReceiveException();
			}

			removePendingAck(outToSender);

			// If all ACKs received, send token to current child
			if (allAcksReceived() && currentChild != null) {
				send(new TokenMessage(), currentChild);
				markSentToken(currentChild);
			}
			return;
		}

		if (m instanceof TokenMessage) {
			// Token returned, pick next child
			currentChild = pickChild();
			if (currentChild != null) {
				send(new TokenMessage(), currentChild);
				markSentToken(currentChild);
			} else {
				done();
			}
			return;
		}

		throw new IllegalReceiveException();
	}
}
