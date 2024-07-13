package Mahjong;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import Mahjong.Meld.MeldType;

class FanCalculatorTest {

    List<Meld> tie(Integer[] tiles, MeldType[] types) {
        List<Meld> melds = new ArrayList<>();
        int counter = 0;

        for (MeldType type : types) {
            Meld meld = new Meld(type);

            int inc = 3;

            if (type == MeldType.Pair) {
                inc = 2;
            }

            for (int i = counter; i < counter + inc; i++) {
                meld.add(new Tile(tiles[i]));
            }

            melds.add(meld);
            counter += 3;
        }

        return melds;
    }

    @Test
    void testCommonHand() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {16, 17, 18, 11, 12, 13, 2, 3, 4, 26, 27, 28, 5, 5};
        MeldType[] handTypes = {MeldType.Seung, MeldType.Seung, MeldType.Seung, MeldType.Seung, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(1, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.COMMON_HAND));
    }

    @Test
    void testAllTriplets() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {16, 16, 16, 100, 100, 100, 26, 26, 26, 500, 500};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Pair};

        Integer[] doorTiles = {7, 7, 7};
        MeldType[] doorTypes = {MeldType.Pong};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = tie(doorTiles, doorTypes);

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(3, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.ALL_TRIPLETS));
    }

    @Test
    void testMixedOneSuit() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {11, 12, 13, 12, 16, 17, 100, 100, 100, 12, 12};
        MeldType[] handTypes = {MeldType.Seung, MeldType.Seung, MeldType.Pong, MeldType.Pair};

        Integer[] doorTiles = {13, 14, 15};
        MeldType[] doorTypes = {MeldType.Seung};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = tie(doorTiles, doorTypes);

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(3, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.MIXED_ONE_SUIT));
    }

    @Test
    void testAllOneSuit() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {1, 2, 3, 2, 3, 4, 5, 6, 7, 8, 8, 8, 4, 4};
        MeldType[] handTypes = {MeldType.Seung, MeldType.Seung, MeldType.Seung, MeldType.Pong, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(7, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.ALL_ONE_SUIT));
    }

    @Test
    void testAllHonorTiles() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {100, 100, 100, 200, 200, 200, 300, 300, 300, 400, 400, 400, 500, 500};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(10, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.ALL_HONOR_TILES));
    }

    @Test
    void testSmallDragons() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {500, 500, 500, 700, 700, 700, 16, 17, 18, 8, 8, 8, 600, 600};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Seung, MeldType.Pong, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(5, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.SMALL_DRAGONS));
    }

    @Test
    void testGreatDragons() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {500, 500, 500, 700, 700, 700, 600, 600, 600, 25, 26, 27, 11, 11};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Seung, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(8, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.GREAT_DRAGONS));
    }

    @Test
    void testSmallWinds() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {100, 100, 100, 200, 200, 200, 300, 300, 300, 16, 17, 18, 400, 400};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Seung, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(6 + 3, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.SMALL_WINDS));
    }

    @Test
    void testGreatWinds() {
        // Arrange: Prepare the data for your test

        Integer[] handTiles = {100, 100, 100, 200, 200, 200, 300, 300, 300, 400, 400, 400, 7, 7};
        MeldType[] handTypes = {MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Pong, MeldType.Pair};

        // Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        System.out.println(fanCalculator.flags);

        assertEquals(13 + 3 + 3, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.GREAT_WINDS));
    }
}