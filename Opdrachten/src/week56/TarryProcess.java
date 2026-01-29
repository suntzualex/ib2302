package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class TarryProcess extends WaveProcess {

    // Track which neighbors have sent a token
    private final java.util.Set<framework.Channel> receivedFrom = new java.util.HashSet<>();
    // Track which neighbors have been sent a token
    private final java.util.Set<framework.Channel> sentTo = new java.util.HashSet<>();
    // Track the parent channel (the first channel from which a token is received)
    private framework.Channel parent = null;

    protected boolean hasReceivedFrom(Channel c) {
        return receivedFrom.contains(c);
    }
    protected void markReceivedFrom(Channel c) {
        receivedFrom.add(c);
    }
    protected boolean hasSentTo(Channel c) {
        return sentTo.contains(c);
    }
    protected void markSentTo(Channel c) {
        sentTo.add(c);
    }
    protected Channel getParent() {
        return parent;
    }
    protected void setParent(Channel c) {
        parent = c;
    }
    protected boolean allNeighborsReceived() {
        return receivedFrom.size() == getIncoming().size();
    }
    protected boolean allNeighborsSent() {
        return sentTo.size() == getOutgoing().size();
    }
    protected Channel pickUnsentNeighbor() {
        // First pass: find any unsent neighbor that is NOT the parent
        for (Channel d : getOutgoing()) {
            if (!hasSentTo(d)) {
                // Check if this outgoing channel goes to the same process that is our parent
                boolean isParentChannel = (getParent() != null &&
                    d.getReceiver().equals(getParent().getSender()));
                if (!isParentChannel) {
                    return d;
                }
            }
        }
        // Second pass: if only parent is left, return it as last resort
        for (Channel d : getOutgoing()) {
            if (!hasSentTo(d)) {
                return d;
            }
        }
        return null;
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
