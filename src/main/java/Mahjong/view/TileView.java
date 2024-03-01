package Mahjong.view;

import Mahjong.Tile;
import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class TileView {
    private Tile tile;
    private GraphicEntityModule graphics;
    private Sprite sprite;

    public TileView(Tile tile, GraphicEntityModule graphicEntityModule) {
        this.tile = tile;
        tile.setView(this);
        this.graphics = graphicEntityModule;

        drawTile();
    }

    private void drawTile() {
        sprite = graphics.createSprite().setImage(tile.getAssetPath()).setX(0).setY(0);
    }
}
