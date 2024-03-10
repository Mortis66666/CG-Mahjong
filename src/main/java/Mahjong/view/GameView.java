package Mahjong.view;

import Mahjong.Constant;
import Mahjong.Game;
import Mahjong.Hand;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.World;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.util.ArrayList;

public class GameView {

    private final World world;
    private final Game game;
    public final GraphicEntityModule graphics;
    private final TooltipModule tooltips;


    public GameView(Game game, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule) {
        this.game = game;
        game.setView(this);
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;
        this.world = graphics.getWorld();

        drawBackground();

//        makePileViews();
        makeHandViews();
        //drawHands();
    }

//    private void makePileViews() {
//        for (Tile tile: game.getPile()) {
//            new TileView(tile, graphics);
//        }
//    }

    private void drawHands() {
        ArrayList<Hand> hands = game.getHands();

        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);

            hand.view.sprite.setY(i * Constant.TILE_HEIGHT);
        }
    }

    private void drawBackground() {
        graphics.createRectangle()
                .setFillColor(0xdbcca0)
                .setWidth(world.getWidth())
                .setHeight(world.getHeight())
                .setX(0).setY(0);
    }

    private void makeHandViews() {
        ArrayList<Hand> hands = game.getHands();
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);
            HandView handView = new HandView(hand, graphics, tooltips);

            handView.setPosition(i);
        }
    }
}
