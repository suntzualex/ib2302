package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class RingInitiator extends RingProcess {

	@Override
	public void init() {
		// Initiator starts active and sends one token to its clockwise neighbor
		send(new TokenMessage(), getClockwiseChannel());
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
		// On first valid token, become passive (finish)
		done();
	}
}
