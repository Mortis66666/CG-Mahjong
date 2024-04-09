package Mahjong;

import Mahjong.view.HandView;

import java.util.*;
import java.util.stream.Collectors;

public class Hand {
    private final ArrayList<Tile> hand;
    private final ArrayList<Tile> discards = new ArrayList<>();
    private final ArrayList<Tile> door = new ArrayList<>();
    private final ArrayList<Meld> doorMelds = new ArrayList<>();
    public HandView view;

    public Hand(ArrayList<Tile> tiles) {
        this.hand = tiles;
        sortHand();
    }

    public void setView(HandView view) {
        this.view = view;
    }

    public boolean isSevenPairs(List<Integer> vec) {
        // Check if the hand is a double like aabbccddeeffgg
        if (vec.size() < 14) return false;

        for (int tile : new HashSet<>(vec)) {
            if (Collections.frequency(vec, tile) != 2) return false;
        }
        return true;
    }

    public boolean isThirteenOrphans(List<Integer> vec) {
        // Check if the hand is a thriteen orphans
        if (vec.size() < 14) return false;

        List<Integer> orphans = new ArrayList<>(Arrays.asList(1, 9, 11, 19, 21, 29, 100, 200, 300, 400, 500, 600, 700));


        for (int tile : new HashSet<>(vec)) {
            if (!orphans.contains(tile)) return false;
        }

        return true;
    }
    public static boolean checkSeq(List<Integer> tiles) {
        List<Integer> temp = new ArrayList<>(tiles);
        while (!temp.isEmpty()) {
            int a = temp.remove(0);
            for (int i = 1; i <= 2; i++) {
                if (!temp.contains(a + i)) {
                    return false;
                }
                temp.remove(Integer.valueOf(a + i));
            }
        }
        return true;
    }
    public boolean isFormat(List<Integer> hand) {
        List<Integer> pairs = new ArrayList<>();
        List<Integer> triplets = new ArrayList<>();
        Set<Integer> uniqueHand = new HashSet<>(hand);
        for (int i : uniqueHand) {
            if (hand.indexOf(i) != hand.lastIndexOf(i)) {
                pairs.add(i);
            }
            if (hand.lastIndexOf(i) - hand.indexOf(i) >= 2) {
                triplets.add(i);
            }
        }
        for (int pair : pairs) {
            List<Integer> hand1 = new ArrayList<>(hand);
            for (int j = 0; j < 2; j++) {
                hand1.remove(Integer.valueOf(pair));
            }
            if (checkSeq(hand1)) {
                return true;
            }
            List<Integer> hand2 = new ArrayList<>(hand1);
            for (int triplet : triplets) {
                if (hand2.indexOf(triplet) != hand2.lastIndexOf(triplet)) {
                    for (int j = 0; j < 3; j++) {
                        hand2.remove(Integer.valueOf(triplet));
                    }
                    if (checkSeq(hand2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canInterrupt(Tile tile, boolean isToTheLeft) {
        if (canPong(tile)) {
            return true;
        }

        if (canSeung(tile, isToTheLeft)) {
            return true;
        }

        return canSik(tile);
    }

    public boolean canSik(Tile discardedTile) {
        ArrayList<Tile> newHand = new ArrayList<>(hand);
        newHand.add(discardedTile);

        List<Integer> intTiles = newHand.stream().mapToInt(Tile::toInteger).boxed().collect(Collectors.toList());

        if (isThirteenOrphans(intTiles) || isSevenPairs(intTiles) || isFormat(intTiles)) {
            return FanCalculator.calculate(intTiles, doorMelds) >= Constant.FAN_MIN;
        }

        return false;
    }

    public boolean canPong(Tile discardedTile) {
        return countTiles(discardedTile) >= 2;
    }

    public boolean canSeung(Tile discardedTile, boolean isToTheLeft) {
        if (!isToTheLeft) return false;
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

    public void pong(Tile pongTarget) {
        Meld meld = new Meld(Meld.MeldType.Pong);
        for (int i = 0; i < 3; i++) {
            Tile target = searchTile(pongTarget.toString());
            meld.add(target);
            doorify(target);
        }
    }

    public void gong(Tile gongTarget) {
        Meld meld = new Meld(Meld.MeldType.Gong);
        for (int i = 0; i < 4; i++) {
            Tile target = searchTile(gongTarget.toString());
            meld.add(target);
            doorify(target);
        }
    }

    public void seung(List<Tile> meldList) {
        Meld meld = new Meld(Meld.MeldType.Seung);
        for (Tile target : meldList) {
            doorify(target);
            meld.add(target);
        }
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

        // Puts all flower in front
        int flowerCount = 0;
        for (Tile doorTile : new ArrayList<>(door)) {
            if (doorTile.isFlower()) {
                door.remove(doorTile);
                door.add(flowerCount, doorTile);
                flowerCount++;
            }
        }
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
