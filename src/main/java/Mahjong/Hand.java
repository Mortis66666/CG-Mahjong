package Mahjong;

import Mahjong.view.HandView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Hand {
    private final ArrayList<Tile> hand;
    private final ArrayList<Tile> discards = new ArrayList<Tile>();
    private final ArrayList<Tile> door = new ArrayList<Tile>();
    public HandView view;

    public Hand(ArrayList<Tile> tiles) {
        this.hand = tiles;
        sortHand();
    }

    public void setView(HandView view) {
        this.view = view;
    }

    public boolean isFormat(List<Tile> vec) {
        if (vec.size() == 0) {
            return true;
        } else if (vec.size() < 3 || vec.size() % 3 != 0) {
            return false;
        }

        boolean res = false;

        // vec[0] is in AAA pattern
        if (vec.get(0).equals(vec.get(1)) && vec.get(1).equals(vec.get(2))) {
            List<Tile> left = vec.subList(3, vec.size());
            if (isFormat(left)) {
                res = true;
            }
        }

        // vec[0] is in ABC pattern
        int i = 0;
        int j = 0;
        for (int k = 1; k < vec.size(); ++k) {
            if (vec.get(k).equals(vec.get(0).inc(1))) {
                i = k;
            }
            if (vec.get(k).equals(vec.get(0).inc(2))) {
                j = k;
            }
        }
        if (i != 0 && j != 0) {
            List<Tile> left = new ArrayList<>();
            for (int k = 1; k < vec.size(); ++k) {
                if (k != i && k != j) {
                    left.add(vec.get(k));
                }
            }
            if (isFormat(left)) {
                res = true;
            }
        }

        return res;
    }

    public boolean canInterrupt(Tile tile) {
        if (canPong(tile)) {
            return true;
        }

        if (canSeung(tile)) {
            return true;
        }

        return canSik(tile);
    }

    public boolean canSik(Tile discardedTile) {
        ArrayList<Tile> newHand = new ArrayList<>(hand);
        newHand.add(discardedTile);

        return isFormat(newHand);
    }

    public boolean canPong(Tile discardedTile) {
        return countTiles(discardedTile) >= 2;
    }

    public boolean canSeung(Tile discardedTile) {
        return (
                (have(discardedTile.inc(1)) && have(discardedTile.inc(2))) ||
                (have(discardedTile.inc(-1)) && have(discardedTile.inc(-2))) ||
                (have(discardedTile.inc(-1)) && have(discardedTile.inc(1)))
        );
    }

    public boolean canGong(Tile discardTile, boolean interrupt) {
        if (interrupt) {
            return countTiles(discardTile) == 3;
        } else {
            return countDoor(discardTile) == 3;
        }
    }

    public int priority(Tile discardedTile) {
        // Check if sikwu
        ArrayList<Tile> newHand = new ArrayList<>(hand);
        newHand.add(discardedTile);

        if (isFormat(newHand)) {
            return 400;
        }

        // Check if pong/gong
        if (countTiles(discardedTile) >= 2) {
            System.out.println("Have" + discardedTile);
            System.out.println(hand);
            return 300;
        }

        return 0;
    }

    public int countTiles(Tile target) {
        int res = 0;
        for (Tile tile : hand) {
            if (tile.equals(target)) res++;
        }

        return res;
    }

    public int countDoor(Tile target) {
        int res = 0;
        for (Tile tile : door) {
            if (tile.equals(target)) res++;
        }

        return res;
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public ArrayList<Tile> getDiscards() {
        return discards;
    }

    public ArrayList<Tile> getDoor() {return door;}

    public Tile searchTile(String tileString) {
        for (Tile tile: hand) {
            if (Objects.equals(tile.toString(), tileString)) {
                return tile;
            }
        }

        return null;
    }

    public Tile searchFromDiscard(String tileString) {
        for (Tile tile: discards) {
            if (Objects.equals(tile.toString(), tileString)) {
                return tile;
            }
        }

        return null;
    }

    public boolean have(String tileString) {
        return searchTile(tileString) != null;
    }

    public boolean have(Tile tile) {
        return have(tile.toString());
    }

    public void doorify(String tileString) {
        Tile tile = searchTile(tileString);

        hand.remove(tile);
        door.add(tile);
    }

    public void doorify(Tile tile) {
        hand.remove(tile);
        door.add(tile);
    }

    public void discardTile(String tileString) {

        Tile tile = searchTile(tileString);

        hand.remove(tile);
        discards.add(tile);

        view.discardTile(tile);
    }

    public void discardTile(Tile tile) {
        hand.remove(tile);
        discards.add(tile);

        view.discardTile(tile);
    }

    public void drawTile(Tile tile) {
        hand.add(tile);
        view.drawTile(tile);
        sortHand();
    }

    public void freeDiscard(Tile tile) {
        discards.remove(tile);
        view.freeDiscard(tile);
    }

    private void sortHand() {
        hand.sort((tile1, tile2) -> {
            // Compare based on priority (smaller value has higher priority)
            double priority1 = tile1.getPriority();
            double priority2 = tile2.getPriority();
            return Double.compare(priority1, priority2);
        });

        door.sort((tile1, tile2) -> {
            // Compare based on priority (smaller value has higher priority)
            double priority1 = tile1.getPriority();
            double priority2 = tile2.getPriority();
            return Double.compare(priority1, priority2);
        });
    }

    public String flowers() {
        StringBuilder result = new StringBuilder();
        for (Tile tile : hand) {
            if (tile.isFlower()) {
                result.append(tile);
            }
        }
        return result.toString();
    }

    public ArrayList<Tile> flowerTiles() {
        ArrayList<Tile> result = new ArrayList<>();
        for (Tile tile : hand) {
            if (tile.isFlower()) {
                result.add(tile);
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Tile tile: hand) {
            result.append(tile.toString());
        }

        return result.toString();
    }
}
