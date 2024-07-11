package Mahjong;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class FanCalculatorTest {

    List<Meld> tie(Integer[] tiles, Meld.MeldType[] types) {
        List<Meld> melds = new ArrayList<>();
        int counter = 0;

        for (Meld.MeldType type : types) {
            Meld meld = new Meld(type);

            int inc = 3;

            if (type == Meld.MeldType.Pair) {
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

        Integer[] handTiles = {16, 17, 18, 10, 11, 12, 1, 2, 3, 26, 27, 28, 5, 5};
        Meld.MeldType[] handTypes = {Meld.MeldType.Seung, Meld.MeldType.Seung, Meld.MeldType.Seung, Meld.MeldType.Pair};

        // TODO: Add some Meld objects to hand and door

        List<Meld> hand = tie(handTiles, handTypes);
        List<Meld> door = new ArrayList<>();

        // Act: Call the method you want to test
        FanCalculator fanCalculator = new FanCalculator(hand, door);

        // Assert: Check if the result is as expected
        assertEquals(1, fanCalculator.fan);
        assertTrue(fanCalculator.flags.contains(FanCalculator.Flag.COMMON_HAND));
    }
}