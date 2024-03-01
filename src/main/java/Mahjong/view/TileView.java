package Mahjong.view;

import Mahjong.Tile;
import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class TileView {
    private Tile tile;
    private GraphicEntityModule graphics;
    private Group sprite;

    public TileView(Tile tile, GraphicEntityModule graphicEntityModule) {
        this.tile = tile;
        tile.setView(this);
        this.graphics = graphicEntityModule;

        drawTile();
    }

    private void drawTile() {
        System.out.println(tile.getAssetPath());
        Sprite faceSprite = graphics.createSprite().setImage(tile.getAssetPath()).setX(0).setY(20).setScale(0.5).setZIndex(1);
        Sprite tileSprite = graphics.createSprite().setImage("tile/00/02.svg").setX(0).setY(0).setScale(0.5).setZIndex(0);
        sprite = graphics.createGroup(faceSprite, tileSprite).setX(0).setY(0);
    }

    public Group getSprite() {
        return sprite;
    }
}
