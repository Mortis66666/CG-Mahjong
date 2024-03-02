package Mahjong.view;

import Mahjong.Constant;
import Mahjong.Game;
import Mahjong.Hand;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.World;

import java.util.ArrayList;

public class GameView {

    private final World world;
    private final Game game;
    public final GraphicEntityModule graphics;


    public GameView(Game game, GraphicEntityModule graphicEntityModule) {
        this.game = game;
        game.setView(this);
        this.graphics = graphicEntityModule;
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
            HandView handView = new HandView(hand, graphics);

            handView.setPosition(i);
        }
    }
}
