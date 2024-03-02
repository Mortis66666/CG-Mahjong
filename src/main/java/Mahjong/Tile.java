package Mahjong;

import Mahjong.view.TileView;

import Mahjong.view.TileView;

public class Tile {

    private int value;
    private TileType type;
    public TileView view;

    public enum TileType {
        Dot, Bamboo, Character, Wind, Dragon, Flower, Season
    }

    public Tile(int value, TileType type) {
        this.value = value;
        this.type = type;
    }

    public void setView(TileView view) {
        this.view = view;
    }

    public int getPriority() {
        return 0;
    }

    public String getAssetPath() {
        return String.format("%s/0%d.svg", type.name().toLowerCase(), value);
    }

    public String toString() {
        return type.name().toLowerCase().charAt(0) + Integer.toString(value);
    }
}
