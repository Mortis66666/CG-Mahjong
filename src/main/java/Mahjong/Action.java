package Mahjong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Action {
    public final int player;
    public final ActionType type;
    public ArrayList<Tile> targets = new ArrayList<>();

    public enum ActionType {
        Draw, Discard, Pong, Seung, Gong, Flower, Bookmark, Pass, Win
    }

    public Action(int committer, ActionType action, ArrayList<Tile> targets) {
        this.player = committer;
        this.type = action;
        this.targets = targets;
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

                if (lastCommitter != (committer == 0 ? 3 : committer - 1)) {
                    throw new InvalidAction("Can only SEUNG the tiles discarded by the player to your left");
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

                if (!hand.canGong(lastDiscardedTile, interrupt)) {
                    throw new InvalidAction("Not enough tiles to perform GONG");
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
                throw new InvalidAction(String.format("Action %s is invalid", inputChunks[1]));
        }

        //        if (expected == -2) {
        //            expected = tilesStrings.size();
        //        }
        //
        //        for (int i = 0; i < expected; i++) {
        //            String tileString = tilesStrings.get(i);
        //
        //            for (int j = 0; j < amount; j++) {
        //                Tile tile = hand.searchTile(tileString);
        //
        //                if (tile == null) {
        //                    if (j > 0) {
        //                        throw new InvalidAction(String.format(
        //                                "Not enough tile %s to perform %s, needed %d, found %d only",
        //                                tilesString,
        //                                type.name(),
        //                                amount,
        //                                j
        //                        ));
        //                    } else {
        //                        throw new InvalidAction(String.format("Tile %s not found to perform %s", tileString, type.name()));
        //                    }
        //                }
        //
        //                targets.add(tile);
        //            }
        //        }
        //
        //        if (extraIndex > -1) {
        //            String tileString = tilesStrings.get(extraIndex);
        //            targets.add(hand.searchTile(tileString));
        //        }

        return new Action(committer, type, targets);
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
