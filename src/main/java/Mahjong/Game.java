package Mahjong;

import Mahjong.view.GameView;

import java.util.ArrayList;
import java.util.Random;

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

        view.graphics.commitWorldState(0);

        switch (action.type) {
            case Discard:
                hand.discardTile(action.target);
                break;
            case Draw:
                Tile drew = drawTile();
                hand.drawTile(drew);
                action.target = drew.toString();
                break;
        }

        view.graphics.commitWorldState(time);
    }

    public int findNextPlayer() {
        if (actions.size() == 0) {
            return 0;
        }

        int last = actions.get(actions.size() - 1).player;
        return last == 3 ? 0 : last + 1;
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

    public Tile drawTile() {
        int randomIndex = random.nextInt(pile.size());
        return pile.remove(randomIndex);
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }
}
