package Mahjong;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FanCalculator {

    public enum Flag {
        THIRTEEN_ORPHANS, SEVEN_PAIRS, COMMON_HAND, ALL_TRIPLETS, MIXED_ONE_SUIT, ALL_ONE_SUIT, ALL_HONOR_TILES,
        SMALL_DRAGONS, GREAT_DRAGONS, SMALL_WINDS, GREAT_WINDS, ALL_GONGS, SELF_TRIPLET, ORPHANS, NINE_GATES
    }

    private final List<Meld> hand;
    private final List<Meld> door;
    public final List<Flag> flags = new ArrayList<>();
    public int fan = 0;

    private int doorPongCount = 0,
            doorSeungCount = 0,
            doorGongCount = 0,
            pongCount = 0,
            seungCount = 0,
            gongCount = 0,
            suitCount = 0,
            windCount = 0,
            dragonCount = 0;

    private Tile.TileType eyeType;


    public FanCalculator(List<Meld> hand, List<Meld> door) {
        // Based on this wikipedia page: https://en.wikipedia.org/wiki/Hong_Kong_mahjong_scoring_rules

        this.hand = hand;
        this.door = door;

        make_stats();

        commonHand();
        allTriplets();
        mixedOneSuit();
        allOneSuit();
        allHonorTiles();
    }

    public FanCalculator(int value) {
        // For special hands like seven pairs and thirteen orphans
        this.hand = null;
        this.door = null;
        fan += value;

        if (value == 4) {
            flags.add(Flag.SEVEN_PAIRS);
        } else if (value == 13) {
            flags.add(Flag.THIRTEEN_ORPHANS);
        }
    }

    private void make_stats() {
        Set<Tile.TileType> types = new HashSet<>();

        for (Meld handMeld: hand) {
            switch (handMeld.type) {
                case Pong:
                    pongCount++;
                    break;
                case Seung:
                    seungCount++;
                    break;
                case Gong:
                    gongCount++;
                    break;
                case Pair:
                    eyeType = handMeld.getTileSuit();
                    break;
            }

            if (handMeld.type != Meld.MeldType.Pair) {
                if (handMeld.getTileSuit() == Tile.TileType.Wind) {
                    windCount++;
                } else if (handMeld.getTileSuit() == Tile.TileType.Dragon) {
                    dragonCount++;
                }
            }

            types.add(handMeld.getTileSuit());
        }

        for (Meld doorMeld: door) {
            switch (doorMeld.type) {
                case Pong:
                    doorPongCount++;
                    break;
                case Seung:
                    doorSeungCount++;
                    break;
                case Gong:
                    doorGongCount++;
                    break;
            }

            if (doorMeld.type != Meld.MeldType.Pair) {
                if (doorMeld.getTileSuit() == Tile.TileType.Wind) {
                    windCount++;
                } else if (doorMeld.getTileSuit() == Tile.TileType.Dragon) {
                    dragonCount++;
                }
            }

            types.add(doorMeld.getTileSuit());
        }

        suitCount = types.size();
    }

    private void commonHand() {
        // Check if every meld is a seung, if true: common hand
        if (Math.max(hand.size() - 1, 0) == seungCount && Math.max(door.size() - 1, 0) == doorSeungCount) {
            flags.add(Flag.COMMON_HAND);
            fan += 1;
        }
    }

    private void allTriplets() {
        // Check if all melds are triplets
        if (hand.size() == pongCount && door.size() == doorPongCount) {
            flags.add(Flag.ALL_TRIPLETS);
            fan += 3;
        }
    }

    private void mixedOneSuit() {
        // Check if all melds are of the same suit (with mixed winds and dragons)
        if (suitCount == 1 && windCount + dragonCount > 0) {
            flags.add(Flag.MIXED_ONE_SUIT);
            fan += 3;
        }
    }

    private void allOneSuit() {
        // Check if all melds are of the same suit
        if (suitCount == 1) {
            flags.add(Flag.ALL_ONE_SUIT);
            fan += 7;
        }
    }

    private void allHonorTiles() {
        // Check if all melds are honor tiles
        if (windCount + dragonCount == 4 && (eyeType == Tile.TileType.Wind || eyeType == Tile.TileType.Dragon)) {
            flags.add(Flag.ALL_HONOR_TILES);
            fan += 10;
        }
    }
}
