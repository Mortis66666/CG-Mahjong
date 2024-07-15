package Mahjong;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Mahjong.Tile.TileType;

public class FanCalculator {

    public enum Flag {
        THIRTEEN_ORPHANS, SEVEN_PAIRS, COMMON_HAND, ALL_IN_TRIPLETS, MIXED_ONE_SUIT, ALL_ONE_SUIT, ALL_HONOR_TILES,
        SMALL_DRAGONS, GREAT_DRAGONS, SMALL_WINDS, GREAT_WINDS, ALL_GONGS, SELF_TRIPLET, ORPHANS, NINE_GATES
    }

    private final List<Meld> hand;
    private final List<Meld> door;
    public final List<Flag> flags = new ArrayList<>();
    public int fan = 0;

    private int
            doorTripletsCount = 0,
            doorPongCount = 0,
            doorSeungCount = 0,
            doorGongCount = 0,
            tripletsCount = 0,
            pongCount = 0,
            seungCount = 0,
            gongCount = 0,
            suitCount = 0,
            windCount = 0,
            dragonCount = 0;

    private TileType eyeType;


    public FanCalculator(List<Meld> hand, List<Meld> door) {
        // Based on this wikipedia page: https://en.wikipedia.org/wiki/Hong_Kong_mahjong_scoring_rules

        this.hand = hand;
        this.door = door;

        make_stats();

        commonHand();
        if (!allHonorTiles()) allInTriplets();
        if (!mixedOneSuit()) allOneSuit();
        if (!greatDragons()) smallDragons();
        if (!greatWinds()) smallWinds();
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
        Set<TileType> types = new HashSet<>();

        for (Meld handMeld : hand) {
            switch (handMeld.type) {
                case Pong:
                    pongCount++;
                    tripletsCount++;
                    break;
                case Seung:
                    seungCount++;
                    tripletsCount++;
                    break;
                case Gong:
                    gongCount++;
                    break;
                case Pair:
                    eyeType = handMeld.getTileSuit();
                    break;
            }

            if (handMeld.type != Meld.MeldType.Pair) {
                if (handMeld.getTileSuit() == TileType.Wind) {
                    windCount++;
                } else if (handMeld.getTileSuit() == TileType.Dragon) {
                    dragonCount++;
                }
            }

            if (handMeld.getTileSuit() != TileType.Wind && handMeld.getTileSuit() != TileType.Dragon)
                types.add(handMeld.getTileSuit());
        }

        for (Meld doorMeld : door) {
            switch (doorMeld.type) {
                case Pong:
                    doorPongCount++;
                    doorTripletsCount++;
                    break;
                case Seung:
                    doorSeungCount++;
                    doorTripletsCount++;
                    break;
                case Gong:
                    doorGongCount++;
                    break;
            }

            if (doorMeld.type != Meld.MeldType.Pair) {
                if (doorMeld.getTileSuit() == TileType.Wind) {
                    windCount++;
                } else if (doorMeld.getTileSuit() == TileType.Dragon) {
                    dragonCount++;
                }
            }

            if (doorMeld.getTileSuit() != TileType.Wind && doorMeld.getTileSuit() != TileType.Dragon)
                types.add(doorMeld.getTileSuit());
        }

        suitCount = types.size();
    }

    private void commonHand() {
        // Check if every meld is a seung, if true: common hand
        if (tripletsCount == seungCount && doorTripletsCount == doorSeungCount) {
            flags.add(Flag.COMMON_HAND);
            fan += 1;
        }
    }

    private void allInTriplets() {
        // Check if all melds are triplets
        if (tripletsCount == pongCount && doorTripletsCount == doorPongCount) {
            flags.add(Flag.ALL_IN_TRIPLETS);
            fan += 3;
        }
    }

    private boolean mixedOneSuit() {
        // Check if all melds are of the same suit (with mixed winds and dragons)
        if (suitCount == 1 && windCount + dragonCount > 0) {
            flags.add(Flag.MIXED_ONE_SUIT);
            fan += 3;
            return true;
        }

        return false;
    }

    private void allOneSuit() {
        // Check if all melds are of the same suit
        if (suitCount == 1) {
            flags.add(Flag.ALL_ONE_SUIT);
            fan += 7;
        }
    }

    private boolean allHonorTiles() {
        // Check if all melds are honor tiles
        if (windCount + dragonCount == 4 && (eyeType == TileType.Wind || eyeType == TileType.Dragon)) {
            flags.add(Flag.ALL_HONOR_TILES);
            fan += 10;
            return true;
        }

        return false;
    }

    private void smallDragons() {
        // Check if there's melds of 2 dragons and a pair of the 3rd dragon
        if (dragonCount == 2 && (eyeType == TileType.Dragon)) {
            flags.add(Flag.SMALL_DRAGONS);
            fan += 5;
        }
    }

    private boolean greatDragons() {
        // Check if there's melds of 3 dragons
        if (dragonCount == 3) {
            flags.add(Flag.GREAT_DRAGONS);
            fan += 8;
            return true;
        }

        return false;
    }

    private void smallWinds() {
        // Check if there's melds of 3 winds and a pair of the 4th wind
        if (windCount == 3 && (eyeType == TileType.Wind)) {
            flags.add(Flag.SMALL_WINDS);
            fan += 6;
        }
    }

    private boolean greatWinds() {
        // Check if there's melds of 4 winds
        if (windCount == 4) {
            flags.add(Flag.GREAT_WINDS);
            fan += 13;
            return true;
        }

        return false;
    }
}
