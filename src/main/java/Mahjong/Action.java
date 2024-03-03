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
        Draw, Discard, Pong, Seung, Gong, Flower
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

    public static Action parse(String input, ArrayList<Hand> hands) throws InvalidAction{
        String[] inputChunks = input.split(" ");

        int committer = Integer.parseInt(inputChunks[0]);
        int amount = 0, expected = 0, extraIndex = -1;
        ActionType type;

        switch (inputChunks[1].toLowerCase()) {
            case "draw":
                type = ActionType.Draw;
                break;
            case "discard":
                type = ActionType.Discard;
                amount = 1;
                expected = 1;
                break;
            case "pong":
                type = ActionType.Pong;
                amount = 3;
                expected = 1;
                extraIndex = 1;
                break;
            case "seung":
                type = ActionType.Seung;
                amount = 1;
                expected = 3;
                extraIndex = 3;
                break;
            case "gong":
                type = ActionType.Gong;
                amount = 4;
                expected = 1;
                break;
            case "flower":
                type = ActionType.Flower;
                amount = 1;
                expected = -2;
                break;
            default:
                throw new InvalidAction(String.format("Action %s is invalid", inputChunks[1]));
        }

        Hand hand = hands.get(committer);
        String tilesString = inputChunks[2];
        List<String> tilesStrings = splitTileStrings(tilesString);
        ArrayList<Tile> targets = new ArrayList<>();

        if (expected == -2) {
            expected = tilesStrings.size();
        }

        for (int i = 0; i < expected; i++) {
            String tileString = tilesStrings.get(i);

            for (int j = 0; j < amount; j++) {
                Tile tile = hand.searchTile(tileString);

                if (tile == null) {
                    if (j > 0) {
                        throw new InvalidAction(String.format(
                                "Not enough tile %s to perform %s, needed %d, found %d only",
                                tilesString,
                                type.name(),
                                amount,
                                j
                        ));
                    } else {
                        throw new InvalidAction(String.format("Tile %s not found to perform %s", tileString, type.name()));
                    }
                }

                targets.add(tile);
            }
        }

        if (extraIndex > -1) {
            String tileString = tilesStrings.get(extraIndex);
            targets.add(hand.searchTile(tileString));
        }

        return new Action(committer, type, targets);
    }

    public String toString() {
        return String.format("%d %s %s", player, type.name(), targets);
    }
}
