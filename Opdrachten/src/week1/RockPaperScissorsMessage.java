package week1;

import framework.Message;

public class RockPaperScissorsMessage implements Message {

	private Item item;
	private int round;

	public RockPaperScissorsMessage(Item item, int round) {
		this.item = item;
		this.round = round;
	}

	public int getRound() {
		return round;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		if (item == Item.ROCK) {
			return "<rock>";
		} else if (item == Item.PAPER) {
			return "<paper>";
		} else {
			return "<scissors>";
		}
	}

	public RockPaperScissorsMessage(Item item) {
		this(item, 1); // Default to round 1 for backward compatibility
	}
}
