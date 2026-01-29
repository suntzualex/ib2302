package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class LaiYangNonInitiator extends LaiYangProcess {

	@Override
	public void init() {
		// Non-initiator does nothing on init
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
