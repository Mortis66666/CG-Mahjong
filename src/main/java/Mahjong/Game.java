package Mahjong;

import Mahjong.view.GameView;
import Mahjong.view.TileView;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {

    private ArrayList<Tile> pile;
    private final Random random;
    public ArrayList<Hand> hands;
    private GameView view;
    public ArrayList<Action> actions;

    public Game(Random random) {
        this.random = random;

        actions = new ArrayList<Action>();

        makePile();
        makeHands();
    }

    public void setView(GameView view) {this.view = view;}

    public void commitAction(Action action, double time) {
        Hand hand = hands.get(action.player);
        Action lastAction = getLastMeaningfulAction();
        actions.add(action);

        switch (action.type) {
            case Discard:
                hand.discardTile(action.targets.get(0));
                break;
            case Draw:
                Tile drew = drawTile();
                hand.drawTile(drew);
                action.targets.add(drew);
                ArrayList<Tile> temp = new ArrayList<>();
                temp.add(drew);
                if (drew.isFlower()) {
                    commitAction(new Action(action.player, Action.ActionType.Flower, temp), -1);
                }
                break;
            case Flower:
                for (Tile s : action.targets) {
                    hand.doorify(s);
                }
                commitAction(new Action(action.player, Action.ActionType.Draw, new ArrayList<>()), -1);
                break;
            case Pong:
                Tile pongTarget = action.targets.get(0);
                hands.get(lastAction.player).freeDiscard(pongTarget);
                hand.drawTile(pongTarget);
                for (int i = 0; i < 3; i++) {
                    hand.doorify(pongTarget.toString());
                }
                commitAction(new Action(action.player, Action.ActionType.Discard, new ArrayList<>(action.targets.subList(1, 2))), -1);
                break;
            case Gong:
                Tile gongTarget = action.targets.get(0);
                hands.get(lastAction.player).freeDiscard(gongTarget);

                if (hand.countTiles(gongTarget) == 3) { // gong is from others
                    hand.drawTile(gongTarget);
                }

                for (int i = 0; i < 4; i++) {
                    hand.doorify(gongTarget.toString());
                }
                break;
            case Seung:
                List<Tile> meld = action.targets.subList(0, 2);
                hands.get(lastAction.player).freeDiscard(meld.get(0));
                hand.drawTile(meld.get(0));
                for (Tile tile : meld) {
                    hand.doorify(tile);
                }
                commitAction(new Action(action.player, Action.ActionType.Discard, new ArrayList<>(action.targets.subList(3, 4))), -1);
                break;
        }


        if (time > 0)
            view.graphics.commitWorldState(time);
    }

    private List<String> splitTileStrings(String input) {
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

    public Action getLastMeaningfulAction() {
        if (actions.size() == 0) return null;

        for (int i = actions.size() - 1; i > -1; i--) {
            Action action = actions.get(i);

            if (action.type.equals(Action.ActionType.Discard) || action.type.equals(Action.ActionType.Gong)) {
                return action;
            }
        }

        return null;
    }

    public ArrayList<Action> getUnknownActions(int playerId) {
        ArrayList<Action> result = new ArrayList<>();

        for (int i = actions.size() - 1; i > -1; i--) {
            Action action = actions.get(i);

            if (action.type.equals(Action.ActionType.Bookmark)) {
                if (action.player == playerId) {
                    break; // Break on bookmark
                } else {
                    continue; // Ignore on other's bookmark
                }
            }

            if (action.player != playerId && action.type.equals(Action.ActionType.Draw)) {
                continue;
            }

            result.add(0, action);
        }

        actions.add(new Action(playerId, Action.ActionType.Bookmark, new ArrayList<>()));
        return result;
    }

    public boolean canPlay(int playerId) {
        Action lastAction = getLastMeaningfulAction();

        if (lastAction == null) {
            return playerId == 0; // When there's no action previously, naturally only the first player can play
        }

        if (lastAction.type.equals(Action.ActionType.Gong)) {
            return playerId == lastAction.player; // When the last action is gong, the player
        }

        Hand hand = hands.get(playerId);


        int expectedNext = lastAction.player == 3 ? 0 : lastAction.player + 1;
        if (playerId == expectedNext) {
            return true;
        }

        return false;
    }

    public int getNextPlayer() {
        Action lastAction = getLastMeaningfulAction();
        if (lastAction == null) return 0;
        if (lastAction.type.equals(Action.ActionType.Gong)) return lastAction.player;
        return lastAction.player == 3 ? 0 : lastAction.player + 1;
    }

    public boolean canInterrupt(int playerId) {
        Hand hand = hands.get(playerId);
        Action action = getLastMeaningfulAction();

        if (action == null || action.type.equals(Action.ActionType.Gong) || action.type.equals(Action.ActionType.Pass)) {
            return false;
        }

        if (action.player == playerId) {
            return false;
        }

        Tile lastDiscarded = action.targets.get(action.targets.size() - 1);
        return hand.canInterrupt(lastDiscarded);
    }

    public ArrayList<Integer> getPlayerPriorities() {

        Action action = getLastMeaningfulAction();

        if (action == null) {
            ArrayList<Integer> result = new ArrayList<>();
            result.add(0);
            result.add(-1);
            result.add(-1);
            result.add(-1);

            return result;
        }

        System.out.println(action.targets);
        int expectedNext = action.player == 3 ? 0 : action.player + 1;

        ArrayList<Integer> scores = new ArrayList<>();

        for (int j = 0; j < 4; j++) {
            int score = -1;
            if (j == expectedNext) {
                score = 0;
            } else if (j != action.player) {
                score += hands.get(j).priority(action.targets.get(action.targets.size() - 1));
            }

            scores.add(score);
        }

        System.out.print("Score:");
        System.out.println(scores);

        return scores;
    }

    private void makePile() {
        pile = new ArrayList<Tile>();

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 10; j++) {
                pile.add(new Tile(j, Tile.TileType.Character));
                pile.add(new Tile(j, Tile.TileType.Bamboo));
                pile.add(new Tile(j, Tile.TileType.Dot));
            }

            for (int j = 1; j < 5; j++) {
                pile.add(new Tile(j, Tile.TileType.Wind));
            }

            for (int j = 1; j < 4; j++) {
                pile.add(new Tile(j, Tile.TileType.Dragon));
            }
        }

        for (int i = 1; i < 5; i++) {
            pile.add(new Tile(i, Tile.TileType.Flower));
            pile.add(new Tile(i, Tile.TileType.Season));
        }
    }

    private void makeHands() {
        hands = new ArrayList<Hand>();
        for (int i = 0; i < 4; i++) {
            ArrayList<Tile> handTiles = new ArrayList<Tile>();

            for (int j = 0; j < 13; j++) {
                handTiles.add(drawTile());
            }

            Hand hand = new Hand(handTiles);
            hands.add(hand);
        }
    }

    public void checkFlowers() {
        for (int i = 0; i < 4; i++) {
            Hand hand = hands.get(i);

            for (ArrayList<Tile> flowers = hand.flowerTiles(); flowers.size() > 0; flowers = hand.flowerTiles()) {
                commitAction(new Action(i, Action.ActionType.Flower, flowers), 0.1);
            }
        }
    }

    public Tile drawTile() {
        int randomIndex = random.nextInt(pile.size());
        return pile.remove(randomIndex);
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }
}
