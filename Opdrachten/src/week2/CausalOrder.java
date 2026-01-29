package week2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import framework.Network;

public class CausalOrder {

	private Set<Pair> pairs = new LinkedHashSet<>();

	public CausalOrder() {
	}

	public CausalOrder(List<Event> sequence) {
		// Build causal pairs from the sequence
		// 1. Process order: for each process, add (e_i, e_{i+1})
		Map<framework.Process, Event> lastEvent = new LinkedHashMap<>();
		Map<String, Event> sendEvents = new LinkedHashMap<>();
		for (Event e : sequence) {
			// Process order
			framework.Process proc = e.getProcess();
			if (lastEvent.containsKey(proc)) {
				addPair(lastEvent.get(proc), e);
			}
			lastEvent.put(proc, e);
			// Message order: send -> receive
			if (e instanceof SendEvent) {
				SendEvent se = (SendEvent) e;
				sendEvents.put(se.getChannel().toString() + ":" + se.getMessage().toString(), e);
			} else if (e instanceof ReceiveEvent) {
				ReceiveEvent re = (ReceiveEvent) e;
				String key = re.getChannel().toString() + ":" + re.getMessage().toString();
				if (sendEvents.containsKey(key)) {
					addPair(sendEvents.get(key), e);
				}
			}
		}
	}

	public Set<List<Event>> toComputation(Set<Event> events) {
		// Return all total orders consistent with the pairs (topological sorts)
		List<Event> eventList = new ArrayList<>(events);
		Set<List<Event>> result = new LinkedHashSet<>();
		boolean[] used = new boolean[eventList.size()];
		List<Event> current = new ArrayList<>();
		Map<Event, Set<Event>> before = new HashMap<>();
		for (Pair p : pairs) {
			before.computeIfAbsent(p.getRight(), k -> new HashSet<>()).add(p.getLeft());
		}
		backtrack(eventList, used, current, before, result);
		return result;
	}

	private void backtrack(List<Event> eventList, boolean[] used, List<Event> current, Map<Event, Set<Event>> before, Set<List<Event>> result) {
		if (current.size() == eventList.size()) {
			result.add(new ArrayList<>(current));
			return;
		}
		for (int i = 0; i < eventList.size(); i++) {
			if (used[i]) continue;
			Event e = eventList.get(i);
			boolean canUse = true;
			if (before.containsKey(e)) {
				for (Event pre : before.get(e)) {
					if (!current.contains(pre)) {
						canUse = false;
						break;
					}
				}
			}
			if (canUse) {
				used[i] = true;
				current.add(e);
				backtrack(eventList, used, current, before, result);
				current.remove(current.size() - 1);
				used[i] = false;
			}
		}
	}

	/*
	 * -------------------------------------------------------------------------
	 */

	@Override
	public boolean equals(Object o) {
		if (o instanceof CausalOrder) {
			CausalOrder that = (CausalOrder) o;
			return this.pairs.equals(that.pairs);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pairs.size();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Pair p : pairs) {
			b.append(" ").append(p);
		}
		return b.toString().trim();
	}

	public void addPair(Event left, Event right) {
		pairs.add(new Pair(left, right));
	}

	public Set<Pair> getPairs() {
		return new LinkedHashSet<>(pairs);
	}

	public static CausalOrder parse(String s, Network n) {

		CausalOrder order = new CausalOrder();

		Map<String, Event> events = new LinkedHashMap<>();

		String[] tokens = s.split(" ");
		for (String token : tokens) {

			String[] subtokens = token.split("<");
			if (subtokens.length != 2) {
				throw new IllegalArgumentException();
			}

			String left = subtokens[0];
			String right = subtokens[1];

			if (!events.containsKey(left)) {
				events.put(left, Event.parse(left, n));
			}
			if (!events.containsKey(right)) {
				events.put(right, Event.parse(right, n));
			}

			order.addPair(events.get(left), events.get(right));
		}

		return order;
	}
}
