package week2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Network;
import framework.Process;

public class VectorClock extends LogicalClock<Map<Process, Integer>> {

	public VectorClock(Map<Process, List<Event>> sequences) {
		// Get all processes in a fixed order
		List<Process> processes = new ArrayList<>(sequences.keySet());
		Map<Process, Integer> processIndex = new LinkedHashMap<>();
		for (int i = 0; i < processes.size(); i++) {
			processIndex.put(processes.get(i), i);
		}
		// For each process, keep a vector
		Map<Process, int[]> vectors = new LinkedHashMap<>();
		for (Process p : processes) {
			vectors.put(p, new int[processes.size()]);
		}
		// For each process, for each event, compute the vector clock
		Map<Event, int[]> eventVectors = new LinkedHashMap<>();
		// Build a global event list in execution order
		Map<Process, Integer> eventPtr = new LinkedHashMap<>();
		for (Process p : processes) eventPtr.put(p, 0);
		boolean progress = true;
		// Build a global list of all events for send/receive matching
		List<Event> allEvents = new ArrayList<>();
		for (List<Event> seq : sequences.values()) allEvents.addAll(seq);
		while (progress) {
			progress = false;
			for (Process p : processes) {
				List<Event> seq = sequences.get(p);
				int idx = eventPtr.get(p);
				if (idx >= seq.size()) continue;
				Event e = seq.get(idx);
				int[] prev = idx > 0 ? eventVectors.get(seq.get(idx-1)).clone() : new int[processes.size()];
				int pi = processIndex.get(p);
				int[] vc = prev.clone();
				if (e instanceof InternalEvent) {
					vc[pi]++;
				} else if (e instanceof SendEvent) {
					vc[pi]++;
				} else if (e instanceof ReceiveEvent) {
					ReceiveEvent re = (ReceiveEvent) e;
					// Find the corresponding send event in all events
					SendEvent se = null;
					for (Event ev : allEvents) {
						if (ev instanceof SendEvent && re.correspondsWith((SendEvent)ev)) {
							se = (SendEvent)ev;
							break;
						}
					}
					if (se == null) throw new IllegalArgumentException("No matching send event for " + re);
					if (!eventVectors.containsKey(se)) continue; // Wait until send event is processed
					int[] sendVC = eventVectors.get(se).clone();
					for (int j = 0; j < vc.length; j++) {
						vc[j] = Math.max(vc[j], sendVC[j]);
					}
					vc[pi]++;
				}
				eventVectors.put(e, vc);
				addTimestamp(e, toMap(vc, processes));
				eventPtr.put(p, idx+1);
				progress = true;
			}
		}
	}

	private Map<Process, Integer> toMap(int[] vc, List<Process> processes) {
		Map<Process, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < vc.length; i++) {
			map.put(processes.get(i), vc[i]);
		}
		return map;
	}

	/*
	 * -------------------------------------------------------------------------
	 */

	public static Map<Process, Integer> parseTimestamp(String s, Network n) {
		String[] tokens = s.split(",");
		List<Process> processes = new ArrayList<>(n.getProcesses().values());
		if (tokens.length != processes.size()) {
			throw new IllegalArgumentException();
		}

		Map<Process, Integer> timestamp = new LinkedHashMap<>();

		for (int i = 0; i< tokens.length; i++) {
			try {
				timestamp.put(processes.get(i), Integer.parseInt(tokens[i]));
			} catch (Throwable t) {
				throw new IllegalArgumentException();
			}
		}

		return timestamp;
	}
}
