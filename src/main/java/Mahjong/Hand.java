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

    public static boolean isFormat(List<String> vec) {
        List<String> line = new ArrayList<>(vec);
        List<Integer> hand = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        for (String i : line) {
            if (i.contains("d")) {
                hand.addAll(temp);
                temp.clear();
            } else if (i.contains("b")) {
                for (int j : temp) {
                    hand.add(j + 10);
                }
                temp.clear();
            } else if (i.contains("c")) {
                for (int j : temp) {
                    hand.add(j + 20);
                }
                temp.clear();
            } else if (i.contains("w")) {
                for (int j : temp) {
                    hand.add(j * 100);
                }
                temp.clear();
            } else if (i.contains("r")) {
                for (int j : temp) {
                    hand.add((j + 4) * 100);
                }
                temp.clear();
            } else {
                temp.add(Integer.parseInt(i));
            }
        }
        hand.sort(null);
        boolean f = false;
        boolean t = true;

        Set<Integer> handSet = new HashSet<>(hand);
        if (handSet.size() == hand.size()) {
            return t;
        }

        Set<Integer> expectedSet = new HashSet<>();
        expectedSet.add(1);
        expectedSet.add(9);
        expectedSet.add(11);
        expectedSet.add(19);
        expectedSet.add(21);
        expectedSet.add(29);
        expectedSet.add(100);
        expectedSet.add(200);
        expectedSet.add(300);
        expectedSet.add(400);
        expectedSet.add(500);
        expectedSet.add(600);
        expectedSet.add(700);
        if (handSet.equals(expectedSet)) {
            return t;
        }

        List<Integer> pairs = new ArrayList<>();
        List<Integer> triplets = new ArrayList<>();
        for (int i : handSet) {
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
                return t;
            }
            List<Integer> hand2 = new ArrayList<>(hand1);
            for (int triplet : triplets) {
                if (hand2.indexOf(triplet) != hand2.lastIndexOf(triplet)) {
                    for (int j = 0; j < 3; j++) {
                        hand2.remove(Integer.valueOf(triplet));
                    }
                    if (checkSeq(hand2)) {
                        return t;
                    }
                }
            }
        }
        return f;
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

        return isFormat(newHand);
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
