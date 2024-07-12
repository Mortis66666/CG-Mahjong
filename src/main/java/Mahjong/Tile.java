package Mahjong;

import Mahjong.view.TileView;

public class Tile {

    public int value;
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
            case Season:
                result = 2;
                break;
            case Flower:
                result = 3;
                break;
            case Dot:
                result = 4;
                break;
            case Bamboo:
                result = 5;
                break;
            case Character:
                result = 6;
                break;
            case Wind:
                result = 7;
                break;
            case Dragon:
                result = 8;
                break;
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

    public int toInteger() {
        int pad = 0;
        switch (type) {
            case Season:
            case Flower:
                pad = -1;
                break;
            case Dot:
                pad = 10;
                break;
            case Bamboo:
                pad = 20;
                break;
            case Dragon:
                pad = 400;
        }

        int reprValue = value;

        if (type.equals(TileType.Wind) || type.equals(TileType.Dragon)) {
            reprValue *= 100;
        }

        return pad + reprValue;
    }

    public Tile(int value) {
        // value being the result of toInteger()
        // Reverse the toInteger() function

        if (value < 10) {
            type = TileType.Character;
            this.value = value;
        } else if (value < 20) {
            type = TileType.Dot;
            this.value = value - 10;
        } else if (value < 30) {
            type = TileType.Bamboo;
            this.value = value - 20;
        } else if (value <= 400) {
            type = TileType.Wind;
            this.value = value / 100;
        } else {
            type = TileType.Dragon;
            this.value = value / 100 - 4;
        }
    }
}
