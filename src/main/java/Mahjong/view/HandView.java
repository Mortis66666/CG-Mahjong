package Mahjong.view;

import Mahjong.Constant;
import Mahjong.Hand;
import Mahjong.Tile;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;

import java.util.ArrayList;

public class HandView {
    private final Hand hand;
    private final GraphicEntityModule graphics;
    public Group sprite;

    public HandView(Hand hand, GraphicEntityModule graphicEntityModule) {
        this.hand = hand;
        hand.setView(this);
        this.graphics = graphicEntityModule;

        sprite = graphics.createGroup();

        makeTileView();
        drawHand();
    }

    private void makeTileView() {
        for (Tile tile: hand.getHand()) {
            new TileView(tile, graphics);
        }
    }

    private void drawHand() {
        ArrayList<Tile> handTiles = hand.getHand();

        for (int i = 0; i < handTiles.size(); i++) {
            Tile tile = handTiles.get(i);
            Group sprite = tile.view.getSprite();

            sprite.setX(i * Constant.TILE_WIDTH).setZIndex(i);
            this.sprite.add(sprite);
        }
    }

    public void setPosition(int n) {
        int height = graphics.getWorld().getHeight();
        int width = graphics.getWorld().getWidth();

        switch (n) {
            case 0:
                sprite.setX(463).setY(height - 120);break;
            case 1:
                sprite.setRotation(-Math.PI / 2).setX(width - 120).setY(height - 43);break;
            case 2:
                sprite.setRotation(Math.PI).setX(width - 463).setY(120);break;
            case 3:
                sprite.setRotation(Math.PI / 2).setX(120).setY(43);break;
        }
    }
}
