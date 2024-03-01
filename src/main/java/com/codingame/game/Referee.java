package com.codingame.game;
import java.util.List;

import Mahjong.view.TileView;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.World;
import com.google.inject.Inject;

import Mahjong.Tile;


public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject MultiplayerGameManager<Player> gameManager;
    @Inject GraphicEntityModule graphicEntityModule;


    @Override
    public void init() {
        gameManager.setMaxTurns(2);

        Tile tile = new Tile(1, Tile.TileType.Character);
        TileView tileView = new TileView(tile, graphicEntityModule);
    }

    @Override
    public void gameTurn(int turn) {
        for (Player player : gameManager.getActivePlayers()) {
            player.sendInputLine("input");
            player.execute();
        }

        for (Player player : gameManager.getActivePlayers()) {
            try {
                List<String> outputs = player.getOutputs();
                // Check validity of the player output and compute the new game state

            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
            }
        }
    }
}
