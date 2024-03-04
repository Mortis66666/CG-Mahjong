package Mahjong;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Action {
    public final int player;
    public final ActionType type;
    public ArrayList<Tile> targets = new ArrayList<>();

    public enum ActionType {
        Draw, Discard, Pong, Seung, Gong, Flower, Bookmark, Pass
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

    public static Action parse(String input, ArrayList<Hand> hands, Tile lastDiscardedTile, boolean interrupt) throws InvalidAction {
        String[] inputChunks = input.split(" ");

        int committer = Integer.parseInt(inputChunks[0]);
        //int amount = 0, expected = 0, extraIndex = -1;
        ActionType type;
        Hand hand = hands.get(committer);
        String tilesString = inputChunks[2];
        List<String> tilesStrings = splitTileStrings(tilesString);
        ArrayList<Tile> targets = new ArrayList<>();

        switch (inputChunks[1].toLowerCase()) {
            case "discard":
                if (!interrupt) throw new InvalidAction("Action DISCARD is prohibited in interrupting turn");

                if (tilesStrings.size() == 0) {
                    throw new InvalidAction("Tile to discard not provided");
                }

                type = ActionType.Discard;
                targets.add(hand.searchTile(tilesStrings.get(0)));
                break;
            case "pong":
                if (!interrupt) throw new InvalidAction("Action PONG is prohibited in non-interrupting turn");

                if (tilesStrings.size() == 0) {
                    throw new InvalidAction("Tile to discard not provided");
                }

                type = ActionType.Pong;
                targets.add(lastDiscardedTile); // Tile that the prev player discard
                targets.add(hand.searchTile(tilesStrings.get(0))); // Tile that player wish to discard
                break;
            case "seung":
                if (!interrupt) throw new InvalidAction("Action SEUNG is prohibited in non-interrupting turn");

                if (tilesStrings.size() < 2) {
                    throw new InvalidAction("Tile to seung not provided enough");
                } else if (tilesStrings.size() < 3) {
                    throw new InvalidAction("Tile to discard not provided");
                }

                type = ActionType.Seung;
                targets.add(lastDiscardedTile); // Tile that the prev player discard

                for (int i = 0; i < 2; i++) {
                    targets.add(hand.searchTile(tilesStrings.get(i))); // Tile that player wish to seung with
                }

                targets.add(hand.searchTile(tilesStrings.get(2))); // Tile that player wish to discard
                break;
            case "gong":
                type = ActionType.Gong;
                targets.add(lastDiscardedTile);
                break;
            case "pass":
                if (!interrupt) throw new InvalidAction("Action PASS is prohibited in non-interrupting turn");
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

    public String toString() {
        StringBuilder targetsDisplay = new StringBuilder();

        for (Tile target : targets) {
            targetsDisplay.append(target);
        }

        return String.format("%d %s %s", player, type.name(), targetsDisplay);
    }
}
