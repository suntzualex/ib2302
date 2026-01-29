package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class RingNonInitiator extends RingProcess {

	@Override
	public void init() {
		// Non-initiator starts active, does nothing until it receives a token
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (!(m instanceof TokenMessage)) {
			throw new IllegalReceiveException();
		}
		if (hasReceivedToken(c)) {
			throw new IllegalReceiveException();
		}
		markTokenReceived(c);
		// Forward token to clockwise neighbor
		send(new TokenMessage(), getClockwiseChannel());
		// Become passive (finish)
		done();
	}
}
