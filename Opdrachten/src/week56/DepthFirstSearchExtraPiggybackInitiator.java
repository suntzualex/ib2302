package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraPiggybackInitiator extends DepthFirstSearchExtraPiggybackProcess {

	@Override
	public void init() {
		visitedIds.clear();
		visitedIds.add(getName());
		Channel child = null;
		for (Channel d : getOutgoing()) {
			child = d;
			break;
		}
		if (child != null) {
			TokenWithIdsMessage token = new TokenWithIdsMessage(getName());
			send(token, child);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (!(m instanceof TokenWithIdsMessage)) throw new IllegalReceiveException();
		if (isPassive()) throw new IllegalReceiveException();
		TokenWithIdsMessage token = (TokenWithIdsMessage) m;
		visitedIds.addAll(token.getIds());
		visitedIds.add(getName());
		if (getParent() == null) setParent(c);
		// Find next unvisited neighbor
		Channel next = null;
		for (Channel d : getOutgoing()) {
			if (!token.getIds().contains(d.getReceiver().getName())) {
				next = d;
				break;
			}
		}
		if (next != null) {
			TokenWithIdsMessage newToken = new TokenWithIdsMessage(getName());
			for (String id : token.getIds()) newToken.addId(id);
			send(newToken, next);
		} else {
			done();
		}
	}
}
