package Mahjong.view;

import Mahjong.Constant;
import Mahjong.Hand;
import Mahjong.Tile;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;

import java.util.ArrayList;

public class HandView {
    private final Hand hand;
    private final GraphicEntityModule graphics;
    public Group handSprite;
    public Group discardSprite;
    public Group doorSprite;
    public Group sprite;

    public HandView(Hand hand, GraphicEntityModule graphicEntityModule) {
        this.hand = hand;
        hand.setView(this);
        this.graphics = graphicEntityModule;

        sprite = graphics.createGroup();
        handSprite = graphics.createGroup();
        discardSprite = graphics.createGroup();
        doorSprite = graphics.createGroup();

        makeTileView();
        initSprite();
        arrangeSprite();
    }

    private void makeTileView() {
        for (Tile tile: hand.getHand()) {
            new TileView(tile, graphics);
        }
    }

    private void initSprite() {
        ArrayList<Tile> handTiles = hand.getHand();

        for (int i = 0; i < handTiles.size(); i++) {
            Tile tile = handTiles.get(i);
            Group sprite = tile.view.getSprite();

            this.handSprite.add(sprite);
        }

        handSprite.setX(0).setY(0);
        this.sprite.add(handSprite);

        discardSprite.setX(0).setY(Constant.DISCARD_START_ROW);
        this.sprite.add(discardSprite);
    }

    public void drawTile(Tile tile) {
        TileView tileView = new TileView(tile, graphics);
        handSprite.add(tileView.getSprite());

        arrangeSprite();
    }

    public void discardTile(Tile tile) {
        Group sprite = tile.view.getSprite();
        //handSprite.remove(sprite);
        //discardSprite.add(sprite);

        sprite.setScale(0.5);

        arrangeSprite();
    }

    public void freeDiscard(Tile tile) {
        sprite.remove(tile.view.getSprite());
    }

    public void arrangeSprite() {
        ArrayList<Tile> handTiles = hand.getHand();

        for (int i = 0; i < handTiles.size(); i++) {
            Tile tile = handTiles.get(i);
            Group sprite = tile.view.getSprite();

            sprite.setX(i * Constant.TILE_WIDTH).setZIndex(i); // Put tile to right
        }

        ArrayList<Tile> door = hand.getDoor();

        for (int i = 0; i < door.size(); i++) {
            Tile tile = door.get(i);
            Group sprite = tile.view.getSprite();

            sprite.setX(i * Constant.TILE_WIDTH).setY(-Constant.TILE_HEIGHT-10).setZIndex(i);
        }

        ArrayList<Tile> discardTiles = hand.getDiscards();

        for (int i = 0; i < discardTiles.size(); i++) {
            Tile tile = discardTiles.get(i);
            Group sprite = tile.view.getSprite();

            int row = Math.floorDiv(i, Constant.DISCARD_PER_ROW);
            int col = i % Constant.DISCARD_PER_ROW;

            sprite.setX((col + 13 - Constant.DISCARD_PER_ROW) * Constant.TILE_WIDTH / 2)
                    .setY(Constant.DISCARD_START_ROW + row * (Constant.TILE_HEIGHT / 2 - 10))
                    .setZIndex((5 - row) * Constant.DISCARD_PER_ROW + col);
        }
    }

    public void setPosition(int n) {
        int height = graphics.getWorld().getHeight();
        int width = graphics.getWorld().getWidth();

        switch (n) {
            case 0:
                handSprite.setX(463).setY(height - 120);break;
            case 1:
                handSprite.setRotation(-Math.PI / 2).setX(width - 120).setY(height - 43);break;
            case 2:
                handSprite.setRotation(Math.PI).setX(width - 463).setY(120);break;
            case 3:
                handSprite.setRotation(Math.PI / 2).setX(120).setY(43);break;
        }
    }
}
