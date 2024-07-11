package Mahjong;

import java.util.ArrayList;
import java.util.List;

public class Meld {

    public enum MeldType {
        Pong, Gong, Seung, Pair
    }

    private final List<Tile> tiles = new ArrayList<>();
    public MeldType type;

    public Meld(MeldType type) {
        this.type = type;
    }

    public void add(Tile tile) {
        tiles.add(tile);
    }

    public Tile.TileType getTileSuit() {
        return tiles.get(0).type;
    }
}
