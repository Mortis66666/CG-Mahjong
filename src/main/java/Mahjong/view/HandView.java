package Mahjong.view;

import Mahjong.Hand;
import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Rectangle;

public class HandView {
    private Hand hand;
    private GraphicEntityModule graphics;

    public HandView(Hand hand, GraphicEntityModule graphicEntityModule) {
        this.hand = hand;
        hand.setView(this);
        this.graphics = graphicEntityModule;
    }
}
