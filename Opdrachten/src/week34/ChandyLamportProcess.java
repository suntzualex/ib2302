package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChandyLamportProcess extends SnapshotProcess {

    protected boolean snapshotInitiated = false;
    private final Map<Channel, List<Message>> channelStates = new HashMap<>();
    private final Map<Channel, Boolean> recordingChannels = new HashMap<>();
    private final Map<Channel, Boolean> markerReceived = new HashMap<>();

    private boolean finished = false;

    @Override
    public void init() {
        // TODO
    }

    @Override
    public void receive(Message m, Channel c) throws IllegalReceiveException {
        System.out.println("Debug: Received message " + m + " on channel " + c);
        if (m == null || c == null) {
            System.out.println("Debug: Null message or channel detected.");
            throw new IllegalReceiveException();
        }

        if (m == Message.DUMMY) {
            System.out.println("Debug: DUMMY message received.");
            throw new IllegalReceiveException();
        }

        if (m instanceof ChandyLamportControlMessage) {
            if (hasFinished()) {
                System.out.println("Debug: Process has already finished.");
                throw new IllegalReceiveException();
            }
            if (markerReceived.getOrDefault(c, false)) {
                System.out.println("Debug: Marker already received on channel " + c);
                throw new IllegalReceiveException();
            }
            markerReceived.put(c, true);
            System.out.println("Debug: Marker received on channel " + c);
            if (!snapshotInitiated) {
                System.out.println("Debug: Initiating snapshot.");
                snapshotInitiated = true;
                startSnapshot();
                recordLocalState();
                sendMarkerMessages();
                // Start recording on all incoming channels except c
                for (Channel in : getIncomingChannels()) {
                    if (!in.equals(c)) startRecording(in);
                }
            }
            stopRecording(c);
            System.out.println("Debug: Stopped recording on channel " + c);
            if (markerReceived.size() == getIncomingChannels().size() && markerReceived.values().stream().allMatch(Boolean::booleanValue)) {
                System.out.println("Debug: All markers received. Setting process as finished.");
                setFinished(true);
                finishSnapshot();
            }
            return;
        }

        // Application message
        if (!snapshotInitiated) {
            System.out.println("Debug: Application message before snapshot started.");
            throw new IllegalReceiveException();
        }
        if (!recordingChannels.getOrDefault(c, false)) {
            System.out.println("Debug: Not recording on channel " + c + ". Message ignored.");
            throw new IllegalReceiveException();
        }
        recordMessage(c, m);
    }

    protected void recordLocalState() {
        System.out.println("Debug: Recording local state.");
        System.out.println("State recorded: " + this);
    }

    protected void sendMarkerMessages() {
        System.out.println("Debug: Sending marker messages.");
        List<Channel> outgoingChannels = getOutgoingChannels();
        if (outgoingChannels != null) {
            for (Channel channel : outgoingChannels) {
                if (channel != null) {
                    System.out.println("Debug: Sending marker to channel " + channel);
                    send(new ChandyLamportControlMessage(), channel);
                }
            }
        }
    }

    protected void startRecording(Channel channel) {
        System.out.println("Debug: Starting recording on channel " + channel);
        recordingChannels.put(channel, true);
        channelStates.put(channel, new ArrayList<>());
    }

    protected void stopRecording(Channel channel) {
        System.out.println("Debug: Stopping recording on channel " + channel);
        recordingChannels.put(channel, false);
    }

    protected void recordMessage(Channel channel, Message message) {
        System.out.println("Debug: Recording message " + message + " on channel " + channel);
        if (recordingChannels.getOrDefault(channel, false)) {
            channelStates.get(channel).add(message);
        }
    }

    protected List<Channel> getOutgoingChannels() {
        return new ArrayList<>(getOutgoing());
    }

    protected List<Channel> getIncomingChannels() {
        return new ArrayList<>(getIncoming());
    }

    @Override
    public boolean hasFinished() {
        return finished;
    }

    protected void setFinished(boolean finished) {
        this.finished = finished;
    }
}
