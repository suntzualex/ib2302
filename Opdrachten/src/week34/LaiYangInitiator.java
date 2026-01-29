package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class LaiYangInitiator extends LaiYangProcess {

	@Override
	public void init() {
		if (!hasStarted()) {
			startSnapshot();
			recordLocalState();
			sendMarkerMessages();
			// Start recording on all incoming channels since this is self-initiated
			for (Channel in : getIncoming()) {
				startRecording(in);
			}
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
