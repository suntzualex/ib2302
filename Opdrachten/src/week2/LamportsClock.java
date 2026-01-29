package week2;

import framework.Process;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class LamportsClock extends LogicalClock<Integer> {
    private int clock;
    private final Map<Process, List<Event>> sequences;

    public LamportsClock() {
        this.clock = 0; // Initialize the clock to 0
        this.sequences = new HashMap<>(); // Initialize the sequences map
    }

    public LamportsClock(Map<Process, List<Event>> sequences) {
        super();
        this.sequences = sequences;
        computeTimestamps();
    }

    private void computeTimestamps() {
        Map<Process, Integer> processClocks = new HashMap<>();
        Map<SendEvent, Integer> sendTimestamps = new HashMap<>();
        Map<Process, Integer> progress = new HashMap<>();
        for (Process p : sequences.keySet()) {
            processClocks.put(p, 0);
            progress.put(p, 0);
        }
        boolean progressMade;
        do {
            progressMade = false;
            for (Process p : sequences.keySet()) {
                List<Event> events = sequences.get(p);
                int idx = progress.get(p);
                if (idx >= events.size()) continue;
                Event event = events.get(idx);
                if (containsTimestamp(event)) continue;
                if (event instanceof InternalEvent) {
                    int clock = processClocks.get(p) + 1;
                    processClocks.put(p, clock);
                    addTimestamp(event, clock);
                    progress.put(p, idx + 1);
                    progressMade = true;
                } else if (event instanceof SendEvent) {
                    int clock = processClocks.get(p) + 1;
                    processClocks.put(p, clock);
                    addTimestamp(event, clock);
                    sendTimestamps.put((SendEvent) event, clock);
                    progress.put(p, idx + 1);
                    progressMade = true;
                } else if (event instanceof ReceiveEvent) {
                    ReceiveEvent re = (ReceiveEvent) event;
                    Process sender = re.getChannel().getSender();
                    List<Event> senderSeq = sequences.get(sender);
                    SendEvent se;
                    try {
                        se = re.getCorrespondingSendEvent(senderSeq);
                    } catch (Exception ex) {
                        continue; // Can't find send event yet, skip for now
                    }
                    if (!containsTimestamp(se)) continue; // Wait for send event
                    int sendTime = getTimestamp(se);
                    int clock = Math.max(processClocks.get(p), sendTime) + 1;
                    processClocks.put(p, clock);
                    addTimestamp(event, clock);
                    progress.put(p, idx + 1);
                    progressMade = true;
                }
            }
        } while (progressMade);
    }

    @Override
    public Map<Event, Integer> getTimestamps() {
        return super.getTimestamps();
    }

    public Integer getTime() {
        System.out.println("Debug: Current clock time = " + clock);
        return clock;
    }

    public void tick() {
        clock++;
        System.out.println("Debug: Clock ticked. New time = " + clock);
    }

    public void update(Integer otherTime) {
        System.out.println("Debug: Updating clock. Current time = " + clock + ", Other time = " + otherTime);
        clock = Math.max(clock, otherTime) + 1;
        System.out.println("Debug: Clock updated. New time = " + clock);
    }
}
