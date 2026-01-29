package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraPiggybackNonInitiator extends DepthFirstSearchExtraPiggybackProcess {

	@Override
	public void init() {
		visitedIds.clear();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (!(m instanceof TokenWithIdsMessage)) throw new IllegalReceiveException();
		if (isPassive()) throw new IllegalReceiveException();
		TokenWithIdsMessage token = (TokenWithIdsMessage) m;
		visitedIds.addAll(token.getIds());
		visitedIds.add(getName());
		if (getParent() == null) setParent(c);
		// Find next unvisited neighbor (not parent)
		Channel next = null;
		for (Channel d : getOutgoing()) {
			if (!d.equals(getParent()) && !token.getIds().contains(d.getReceiver().getName())) {
				next = d;
				break;
			}
		}
		if (next != null) {
			TokenWithIdsMessage newToken = new TokenWithIdsMessage(getName());
			for (String id : token.getIds()) newToken.addId(id);
			send(newToken, next);
		} else {
			// All visited, return to parent
			Channel outgoingToParent = null;
			for (Channel out : getOutgoing()) {
				if (out.getReceiver().getName().equals(getParent().getSender().getName())) {
					outgoingToParent = out;
					break;
				}
			}
			if (outgoingToParent != null) {
				TokenWithIdsMessage returnToken = new TokenWithIdsMessage(getName());
				for (String id : token.getIds()) returnToken.addId(id);
				send(returnToken, outgoingToParent);
			}
			done();
		}
	}
}
