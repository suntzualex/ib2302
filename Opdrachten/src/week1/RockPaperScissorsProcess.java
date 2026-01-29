package week1;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;
import java.util.HashSet;
import java.util.Set;

public class RockPaperScissorsProcess extends Process {

    private final Set<String> receivedFrom;
    private final Item myItem;
    private final Set<RockPaperScissorsMessage> receivedMessages = new HashSet<>();

    public RockPaperScissorsProcess() {
        this.receivedFrom = new HashSet<>();
        this.myItem = Item.random(); // kies willekeurig steen, papier of schaar
    }

    @Override
    public void init() {
        for (Channel channel : getOutgoing()) {
            send(new RockPaperScissorsMessage(myItem), channel);
        }
    }

    @Override
    public void receive(Message m, Channel c) throws IllegalReceiveException {
        if (!(m instanceof RockPaperScissorsMessage)) {
            throw new IllegalReceiveException();
        }

        String sender = c.getSender().getName();
        if (receivedFrom.contains(sender)) {
            throw new IllegalReceiveException();
        }

        receivedFrom.add(sender);
        receivedMessages.add((RockPaperScissorsMessage) m);

        // Check if all messages are received
        if (receivedFrom.size() == getOutgoing().size()) {
            decideOutcome();
        }
    }

    private void decideOutcome() {
        boolean hasWon = false;
        boolean hasLost = false;

        //System.out.println("Debug: My item is " + myItem);

        for (RockPaperScissorsMessage message : receivedMessages) {
            Item opponentItem = message.getItem();

            //System.out.println("Debug: Received item " + opponentItem);

            if ((myItem == Item.ROCK && opponentItem == Item.SCISSORS) ||
                (myItem == Item.PAPER && opponentItem == Item.ROCK) ||
                (myItem == Item.SCISSORS && opponentItem == Item.PAPER)) {
                hasWon = true;
            } else if ((opponentItem == Item.ROCK && myItem == Item.SCISSORS) ||
                       (opponentItem == Item.PAPER && myItem == Item.ROCK) ||
                       (opponentItem == Item.SCISSORS && myItem == Item.PAPER)) {
                hasLost = true;
            }
        }

        //System.out.println("Debug: hasWon = " + hasWon + ", hasLost = " + hasLost);
        print(hasWon + " " + hasLost);
    }
}
