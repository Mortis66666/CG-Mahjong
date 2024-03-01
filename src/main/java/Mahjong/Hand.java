package Mahjong;

import Mahjong.view.HandView;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Tile> hand;
    private HandView view;

    public Hand(ArrayList<Tile> tiles) {
        this.hand = tiles;
    }

    public void setView(HandView view) {
        this.view = view;
    }

    public boolean isWinning() {
        // TODO
        return false;
    }


}
