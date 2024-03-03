package Mahjong;

import Mahjong.view.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
        actions.add(action);

        Hand hand = hands.get(action.player);

        switch (action.type) {
            case Discard:
                hand.discardTile(action.target);
                break;
            case Draw:
                Tile drew = drawTile();
                hand.drawTile(drew);
                action.target = drew.toString();

                if (drew.isFlower()) {
                    commitAction(new Action(action.player, Action.ActionType.Flower, drew.toString()), -1);
                }
                break;
            case Flower:
                for (String s: splitTileStrings(action.target)) {
                    hand.doorify(s);
                }
                commitAction(new Action(action.player, Action.ActionType.Draw, "_"), -1);
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

    public int findNextPlayer() {
        for (int i = actions.size() - 1; i > -1; i--) {
            Action action = actions.get(i);

            if (action.type.equals(Action.ActionType.Discard)) {
                return action.player == 3 ? 0 : action.player + 1;
            }
        }

        return 0;
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

            for (String flowers = hand.flowers(); !Objects.equals(flowers, ""); flowers = hand.flowers()) {
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
