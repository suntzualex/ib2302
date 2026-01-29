package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class DepthFirstSearchExtraPiggybackProcess extends WaveProcess {

	protected java.util.Set<String> visitedIds = new java.util.HashSet<>();
	protected Channel parent = null;
	protected void setParent(Channel c) { parent = c; }
	protected Channel getParent() { return parent; }

	@Override
	public void init() {
		// Subclasses implement initialization logic (e.g., initiator starts the wave)
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// Subclasses implement message handling for DFS traversal with piggybacked data
	}
}
