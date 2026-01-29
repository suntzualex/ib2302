package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class RingProcess extends WaveProcess {

    // Track if a token has been received from each incoming channel
    private final java.util.Set<framework.Channel> receivedTokenFrom = new java.util.HashSet<>();

    protected boolean hasReceivedToken(Channel c) {
        return receivedTokenFrom.contains(c);
    }

    protected void markTokenReceived(Channel c) {
        receivedTokenFrom.add(c);
    }

    protected Channel getClockwiseChannel() {
        // There is only one outgoing channel in the ring
        return getOutgoing().iterator().next();
    }

    @Override
    public void init() {
        // Default: do nothing, subclasses override
    }

    @Override
    public void receive(Message m, Channel c) throws IllegalReceiveException {
        // Default: do nothing, subclasses override
    }
}
