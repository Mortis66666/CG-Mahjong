package Mahjong;

import Mahjong.view.TileView;

import Mahjong.view.TileView;

public class Tile {

    private int value;
    public TileType type;
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

    public double getPriority() {
        int result = 0;

        switch (type) {
            case Season:result=2;break;
            case Flower:result=3;break;
            case Dot:result=4;break;
            case Bamboo:result=5;break;
            case Character:result=6;break;
            case Wind:result=7;break;
            case Dragon:result=8;break;
        }

        return result * 9 + value;
    }

    public String getAssetPath() {
        return String.format("%s/0%d.svg", type.name().toLowerCase(), value);
    }

    public String toString() {
        char c = type.equals(TileType.Dragon) ? 'r' : type.name().toLowerCase().charAt(0);
        return c + Integer.toString(value);
    }

    public boolean isFlower() {
        return type.equals(Tile.TileType.Flower) || type.equals(Tile.TileType.Season);
    }

    public Tile inc(int n) {
        return new Tile(value + n, type);
    }

    public boolean equals(Tile other) {
        return toString().equals(other.toString());
    }
}
