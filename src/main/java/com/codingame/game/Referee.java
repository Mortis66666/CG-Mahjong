package com.codingame.game;

import Mahjong.Action;
import Mahjong.Game;
import Mahjong.Tile;
import Mahjong.view.GameView;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject MultiplayerGameManager<Player> gameManager;
    @Inject GraphicEntityModule graphicEntityModule;

    private Game game;

    @Override
    public void init() {
        gameManager.setMaxTurns(80);


        game = new Game(gameManager.getRandom());
        new GameView(game, graphicEntityModule);


    }

    @Override
    public void gameTurn(int turn) {
        if (turn == 1) {
            for (int i = 0; i < gameManager.getPlayerCount(); i++) {
                Player player = gameManager.getPlayer(i);
                player.sendInputLine(game.hands.get(i).toString());

                game.checkFlowers();
            }
        }

        int playerId = game.findNextPlayer();
        Player player = gameManager.getPlayer(playerId);

        player.sendInputLine("input");
        player.execute();

        try {
            List<String> outputs = player.getOutputs();

            game.commitAction(new Action(playerId, Action.ActionType.Draw, new ArrayList<>()), 0.33);
            ArrayList<Tile> temp = new ArrayList<Tile>();
            temp.add(game.getHands().get(playerId).getHand().get(0));
            game.commitAction(new Action(playerId, Action.ActionType.Discard, temp), 0.66);

        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
        }
    }
}
