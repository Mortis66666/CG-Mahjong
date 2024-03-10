package com.codingame.game;

import Mahjong.Action;
import Mahjong.Game;
import Mahjong.InvalidAction;
import Mahjong.Tile;
import Mahjong.view.GameView;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

import java.util.*;


public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject MultiplayerGameManager<Player> gameManager;
    @Inject GraphicEntityModule graphicEntityModule;
    @Inject TooltipModule tooltipModule;

    private Game game;
    private boolean alreadyPassed = false;

    @Override
    public void init() {
        System.out.printf("=======================seed=%d=======================%n", gameManager.getSeed());
        gameManager.setMaxTurns(80);

        game = new Game(gameManager.getRandom());
        new GameView(game, graphicEntityModule, tooltipModule);

        for (Player player : gameManager.getActivePlayers()) {
            game.initPlayer(player.getIndex(), player);
        }
    }

    @Override
    public void gameTurn(int turn) {
        if (turn == 1) {
            for (int i = 0; i < gameManager.getPlayerCount(); i++) {
                Player player = gameManager.getPlayer(i);
                player.sendInputLine(String.valueOf(i));
                player.sendInputLine(game.hands.get(i).toString());
            }

            game.checkFlowers();
        }

        if (!alreadyPassed) {
            Action lastMeaningfulAction = game.getLastMeaningfulAction();

            if (lastMeaningfulAction != null) {
                Tile lastDiscardedTile = lastMeaningfulAction.targets.get(lastMeaningfulAction.targets.size() - 1);

                Boolean[] cache = {false, false, false, false};
                for (Player player : gameManager.getActivePlayers()) {
                    int playerId = player.getIndex();

                    if (game.canInterrupt(playerId)) {
                        System.out.printf("%d can interrupt%n", playerId);
                        ArrayList<Action> unknownActions = game.getUnknownActions(playerId);

                        player.sendInputLine("interrupt");
                        player.sendInputLine(String.valueOf(unknownActions.size()));

                        for (Action action : unknownActions) {
                            player.sendInputLine(action.toString());
                        }

                        cache[playerId] = true;
                        player.execute();
                    }
                }

                System.out.println(Arrays.toString(cache));

                ArrayList<Action> actions = new ArrayList<>();
                for (Player player : gameManager.getActivePlayers()) {
                    int playerId = player.getIndex();

                    if (cache[playerId]) {
                        try {
                            List<String> outputs = player.getOutputs();
                            actions.add(Action.parse(outputs.get(0), player.getIndex(), game.getHands(), lastDiscardedTile, true));
                        } catch (TimeoutException e) {
                            player.deactivate(String.format("$%d timeout!", player.getIndex()));
                            System.out.println("timeout bruh");
                            gameManager.addToGameSummary(String.format("$%d timeout!", player.getIndex()));
                            gameManager.endGame();
                            return;
                        } catch (InvalidAction e) {
                            player.deactivate(e.getMessage());
                            System.out.println(e.getMessage());
                            gameManager.addToGameSummary(e.getMessage());
                            gameManager.endGame();
                            return;
                        }
                    }
                }

                System.out.println(actions);

                if (actions.size() > 0) { // Interrupting is an option for at least one person
                    Action action = Collections.max(actions, Comparator.comparing(Action::getPriority));

                    if (!action.type.equals(Action.ActionType.Pass)) {
                        game.commitAction(action, 0.5);
                    } else {
                        alreadyPassed = true;
                    }

                    return;
                }
            }
        }


        Player player = gameManager.getPlayer(game.getNextPlayer());
        System.out.printf("Turn of %d%n", player.getIndex());

        Action drawAction = new Action(player.getIndex(), Action.ActionType.Draw, new ArrayList<>());
        game.commitAction(drawAction, 0.33);

        ArrayList<Action> unknownActions = game.getUnknownActions(player.getIndex());

        player.sendInputLine("normal");
        player.sendInputLine(String.valueOf(unknownActions.size()));

        for (Action action : unknownActions) {
            player.sendInputLine(action.toString());
        }

        player.execute();

        try {
            List<String> outputs = player.getOutputs();
            game.commitAction(Action.parse(outputs.get(0), player.getIndex(), game.getHands(), drawAction.targets.get(0), false), 0.66);
        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
            gameManager.addToGameSummary(String.format("$%d timeout!", player.getIndex()));
            gameManager.endGame();
        } catch (InvalidAction e) {
            player.deactivate(e.getMessage());
            gameManager.addToGameSummary(e.getMessage());
            gameManager.endGame();
        }

        alreadyPassed = false;
    }

    @Override
    public void onEnd() {
        System.out.println("over");
    }
}
