package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlNonInitiator extends DepthFirstSearchExtraControlProcess {

	private Channel currentChild = null;

	@Override
	public void init() {
		// Non-initiator does nothing on init
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {

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

			// If all ACKs received, send token to child or return to parent
			if (allAcksReceived()) {
				if (currentChild != null) {
					send(new TokenMessage(), currentChild);
					markSentToken(currentChild);
				} else {
					// No child, return to parent and finish
					if (parent != null) {
						String parentName = parent.getSender().getName();
						Channel outToParent = findOutgoingTo(parentName);
						if (outToParent != null) {
							send(new TokenMessage(), outToParent);
							markSentToken(outToParent);
						}
					}
					done();
				}
			}
			return;
		}

		if (m instanceof TokenMessage) {
			if (isPassive()) {
				throw new IllegalReceiveException();
			}

			if (parent == null) {
				// First token sets parent
				setParent(c);

				// Pick child
				currentChild = pickChild();

				if (currentChild != null) {
					// Send INFO to all neighbors except parent and chosen child
					for (Channel out : getOutgoing()) {
						boolean isParent = out.getReceiver().getName().equals(parent.getSender().getName());
						if (!isParent && !out.equals(currentChild)) {
							send(new InfoMessage(), out);
							markSentInfo(out);
							addPendingAck(out);
						}
					}

					// If no INFO to send, forward token immediately
					if (allAcksReceived()) {
						send(new TokenMessage(), currentChild);
						markSentToken(currentChild);
					}
				} else {
					// No child available, return to parent and finish immediately
					String parentName = parent.getSender().getName();
					Channel outToParent = findOutgoingTo(parentName);
					if (outToParent != null) {
						send(new TokenMessage(), outToParent);
						markSentToken(outToParent);
					}
					done();
				}
			} else {
				// Token returned from child, pick next child or return to parent
				currentChild = pickChild();
				if (currentChild != null) {
					send(new TokenMessage(), currentChild);
					markSentToken(currentChild);
				} else {
					// No more children, return to parent and finish
					String parentName = parent.getSender().getName();
					Channel outToParent = findOutgoingTo(parentName);
					if (outToParent != null) {
						send(new TokenMessage(), outToParent);
						markSentToken(outToParent);
					}
					done();
				}
			}
			return;
		}

		throw new IllegalReceiveException();
	}
}
