package week1;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

import java.util.HashSet;
import java.util.Set;

public class RockPaperScissorsCheatingProcess extends Process {

    private Item myItem;

    private final Set<String> receivedFrom = new HashSet<>();
    private final Set<RockPaperScissorsMessage> receivedMessages = new HashSet<>();

    @Override
    public void init() {
        // Do nothing initially, wait for others to send their items
    }

    @Override
    public void receive(Message m, Channel c) throws IllegalReceiveException {
        if (!(m instanceof RockPaperScissorsMessage)) {
            throw new IllegalReceiveException();
        }

        // Collect all incoming messages
        String sender = c.getSender().getName();
        RockPaperScissorsMessage message = (RockPaperScissorsMessage) m;

        System.out.println("Debug: Received message from " + sender + " with item " + message.getItem());

        // Check if the sender has already sent a message
        if (receivedFrom.contains(sender)) {
            System.out.println("Debug: Sender " + sender + " has already sent a message.");
            throw new IllegalReceiveException();
        }

        System.out.println("Debug: Adding sender " + sender + " to receivedFrom.");
        receivedFrom.add(sender);
        receivedMessages.add(message);

        // Once all messages are received, decide on a "winning" item
        if (receivedFrom.size() == getIncoming().size()) {
            System.out.println("Debug: All messages received. Deciding on a winning item.");
            decideWinningItem();
            for (Channel channel : getOutgoing()) {
                System.out.println("Debug: Sending item " + myItem + " to channel " + channel);
                send(new RockPaperScissorsMessage(myItem), channel);
            }
        }
    }

    private void decideWinningItem() {
        System.out.println("Debug: Deciding winning item based on received messages.");
        boolean rockBeats = false;
        boolean paperBeats = false;
        boolean scissorsBeats = false;
        boolean rockLoses = false;
        boolean paperLoses = false;
        boolean scissorsLoses = false;

        for (RockPaperScissorsMessage message : receivedMessages) {
            Item opponentItem = message.getItem();
            System.out.println("Debug: Evaluating opponent item " + opponentItem);
            if (opponentItem == Item.ROCK) {
                paperBeats = true;
                scissorsLoses = true;
            } else if (opponentItem == Item.PAPER) {
                scissorsBeats = true;
                rockLoses = true;
            } else if (opponentItem == Item.SCISSORS) {
                rockBeats = true;
                paperLoses = true;
            }
        }

        System.out.println("Debug: rockBeats=" + rockBeats + ", paperBeats=" + paperBeats + ", scissorsBeats=" + scissorsBeats);
        System.out.println("Debug: rockLoses=" + rockLoses + ", paperLoses=" + paperLoses + ", scissorsLoses=" + scissorsLoses);

        if (rockBeats && !rockLoses) {
            myItem = Item.ROCK;
        } else if (paperBeats && !paperLoses) {
            myItem = Item.PAPER;
        } else {
            myItem = Item.SCISSORS;
        }

        System.out.println("Debug: Chosen item is " + myItem);

        boolean hasWon = false;
        boolean hasLost = false;

        for (RockPaperScissorsMessage message : receivedMessages) {
            Item opponentItem = message.getItem();

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

        System.out.println("Debug: hasWon = " + hasWon + ", hasLost = " + hasLost);
        print(hasWon + " " + hasLost);
    }
}
