package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class TarryNonInitiator extends TarryProcess {

	@Override
	public void init() {
		// Non-initiator: do nothing until token received
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (!(m instanceof TokenMessage)) {
			throw new IllegalReceiveException();
		}
		if (isPassive()) {
			throw new IllegalReceiveException();
		}
		if (hasReceivedFrom(c)) {
			throw new IllegalReceiveException();
		}

		markReceivedFrom(c);
		if (getParent() == null) {
			setParent(c);
		}

		// Use the same logic as TarryInitiator for consistency
		Channel toSend = pickUnsentNeighbor();
		if (toSend != null) {
			send(new TokenMessage(), toSend);
			markSentTo(toSend);
		}

		// Check if all neighbors have sent tokens to us - if so, we're done
		if (allNeighborsReceived()) {
			done();
		}
	}
}
