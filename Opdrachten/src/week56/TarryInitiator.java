package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class TarryInitiator extends TarryProcess {

	@Override
	public void init() {
		// Initiator: send token to any neighbor
		Channel d = pickUnsentNeighbor();
		if (d != null) {
			send(new TokenMessage(), d);
			markSentTo(d);
		}
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

		// Forward token to any unsent neighbor (initiator has no parent restriction)
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
