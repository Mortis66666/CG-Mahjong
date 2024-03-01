package Mahjong;

import Mahjong.view.GameView;
import Mahjong.view.HandView;
import com.codingame.gameengine.module.entities.GraphicEntityModule;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    private ArrayList<Tile> pile;
    private Random random;
    private ArrayList<Hand> hands;
    private GameView view;

    public Game(Random random) {
        this.random = random;

        makePile();
        makeHands();
    }

    public void setView(GameView view) {this.view = view;}

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
