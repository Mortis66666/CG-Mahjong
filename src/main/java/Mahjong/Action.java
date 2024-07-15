package Mahjong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Mahjong.Constant.FAN_MIN;

public class Action {
    public final int player;
    public final ActionType type;
    public ArrayList<Tile> targets = new ArrayList<>();
    public FanCalculator calculator;

    public enum ActionType {
        Draw, Discard, Pong, Seung, Gong, Flower, Bookmark, Pass, Win
    }

    public Action(int committer, ActionType action, ArrayList<Tile> targets) {
        this.player = committer;
        this.type = action;
        this.targets = targets;
    }

    public Action(int committer, ActionType action, ArrayList<Tile> targets, FanCalculator calculator) {
        this.player = committer;
        this.type = action;
        this.targets = targets;
        this.calculator = calculator;
    }

    private static List<String> splitTileStrings(String input) {
        List<String> pairs = new ArrayList<>();

        // Define the regular expression pattern
        Pattern pattern = Pattern.compile("[a-z]\\d");
        Matcher matcher = pattern.matcher(input);

        // Find and extract each matching substring
        while (matcher.find()) {
            pairs.add(matcher.group());
        }

        return pairs;
    }

    public static Action parse(String input, int committer, ArrayList<Hand> hands, int lastCommitter, Tile lastDiscardedTile, boolean interrupt) throws InvalidAction {
        String[] inputChunks = input.split(" ");

        //int amount = 0, expected = 0, extraIndex = -1;
        ActionType type;
        FanCalculator calculator = new FanCalculator(0);
        Hand hand = hands.get(committer);
        String tilesString = "";

        if (inputChunks.length >= 2) {
            tilesString = inputChunks[1];
        }

        List<String> tilesStrings = splitTileStrings(tilesString);
        ArrayList<Tile> targets = new ArrayList<>();
        String discardString;
        Tile wishDiscard;

        switch (inputChunks[0].toLowerCase()) {
            case "discard":
                if (interrupt) {
                    throw new InvalidAction("Action DISCARD is prohibited in interrupting turn");
                }
                if (tilesStrings.size() == 0) {
                    throw new InvalidAction("Tile to discard not provided");
                }
                type = ActionType.Discard;

                discardString = tilesStrings.get(0);
                Tile tile = hand.searchTile(discardString);

                if (tile == null) {
                    throw new InvalidAction(String.format("Tile to discard (%s) not found", discardString));
                }

                targets.add(tile);
                break;
            case "pong":
                if (!interrupt) {
                    throw new InvalidAction("Action PONG is prohibited in non-interrupting turn");
                }
                if (tilesStrings.size() == 0) {
                    throw new InvalidAction("Tile to discard not provided");
                }

                if (!hand.canPong(lastDiscardedTile)) {
                    throw new InvalidAction("Not enough tiles to perform PONG");
                }

                discardString = tilesStrings.get(0);
                wishDiscard = hand.searchTile(discardString);

                if (wishDiscard == null) {
                    throw new InvalidAction(String.format("Tile to discard (%s) not found", discardString));
                }

                if (wishDiscard.equals(lastDiscardedTile) && hand.countTiles(wishDiscard) < 3) {
                    throw new InvalidAction(String.format("Tile to discard (%s) is used to perform action PONG", discardString));
                }

                type = ActionType.Pong;
                targets.add(lastDiscardedTile); // Tile that the prev player discard
                targets.add(wishDiscard); // Tile that player wish to discard
                break;
            case "seung":
                if (!interrupt) {
                    throw new InvalidAction("Action SEUNG is prohibited in non-interrupting turn");
                }
                if (tilesStrings.size() < 2) {
                    throw new InvalidAction("Tile to seung not provided enough");
                } else if (tilesStrings.size() < 3) {
                    throw new InvalidAction("Tile to discard not provided");
                }

                if (lastCommitter != (committer == 0 ? 3 : committer - 1)) {
                    throw new InvalidAction("Can only SEUNG the tiles discarded by the player to your left");
                }

                discardString = tilesStrings.get(2);
                wishDiscard = hand.searchTile(discardString);

                if (wishDiscard == null) {
                    throw new InvalidAction(String.format("Tile to discard (%s) not found", discardString));
                }

                Integer[] meld = new Integer[3];

                type = ActionType.Seung;
                targets.add(lastDiscardedTile); // Tile that the prev player discard
                meld[0] = lastDiscardedTile.value;
                for (int i = 0; i < 2; i++) {
                    Tile t = hand.searchTile(tilesStrings.get(i));
                    targets.add(t); // Tile that player wish to seung with
                    meld[i + 1] = t.value;
                }
                targets.add(wishDiscard); // Tile that player wish to discard

                if (!targets.get(0).type.equals(targets.get(1).type) || !targets.get(1).type.equals(targets.get(2).type)) {
                    throw new InvalidAction("Only tiles with same suits are allowed to perform SEUNG");
                }

                Arrays.sort(meld);
                if (meld[1] - meld[0] != 1 || meld[2] - meld[1] != 1) {
                    throw new InvalidAction("Tiles to perform SEUNG must be consecutive");
                }
                break;
            case "gong":
                type = ActionType.Gong;

                if (!interrupt && tilesStrings.size() == 0) {
                    throw new InvalidAction("Tile to GONG not provided");
                }

                Tile targetTile = interrupt ? lastDiscardedTile : hand.searchTile(tilesStrings.get(0));

                if (!hand.canGong(targetTile, interrupt)) {
                    throw new InvalidAction("Not enough tiles to perform GONG");
                }

                targets.add(lastDiscardedTile);
                break;
            case "win":
                type = ActionType.Win;

                calculator = hand.getCalculator(lastDiscardedTile);

                if (calculator.fan == -1) {
                    throw new InvalidAction("Not enough tiles to perform WIN");
                }

                if (calculator.fan < FAN_MIN) {
                    throw new InvalidAction(String.format("Fan count (%d) is less than minimum required (%d)", calculator.fan, FAN_MIN));
                }

                targets.add(lastDiscardedTile);
                break;
            case "pass":
                if (!interrupt) {
                    throw new InvalidAction("Action PASS is prohibited in non-interrupting turn");
                }
                type = ActionType.Pass;

                break;
            default:
                throw new InvalidAction(String.format("Action %s is invalid", inputChunks[0]));
        }

        return new Action(committer, type, targets, calculator);
    }

    public int getPriority() {
        switch (type) {
            case Pong:
            case Gong:
                return 300;
            case Seung:
                return 200;
            case Pass:
                return 0;
        }

        return 0;
    }

    public String toString() {
        StringBuilder targetsDisplay = new StringBuilder();

        for (Tile target : targets) {
            targetsDisplay.append(target);
        }

        return String.format("%d %s %s", player, type.name().toUpperCase(), targetsDisplay);
    }
}
