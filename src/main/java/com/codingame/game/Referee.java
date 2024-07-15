package com.codingame.game;

import Mahjong.Action;
import Mahjong.Game;
import Mahjong.InvalidAction;
import Mahjong.Tile;
import Mahjong.view.GameView;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

import java.lang.reflect.Array;
import java.util.*;


public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject
    MultiplayerGameManager<Player> gameManager;
    @Inject
    GraphicEntityModule graphicEntityModule;
    @Inject
    TooltipModule tooltipModule;
    @Inject
    EndScreenModule endScreenModule;

    private Game game;
    private boolean alreadyPassed = false;
    private Player winner = null;

    @Override
    public void init() {
        System.out.printf("=======================seed=%d=======================%n", gameManager.getSeed());
        gameManager.setMaxTurns(300);

        // Make a rigged hand
//        List<List<String>> riggedHands = new ArrayList<>();
//        String[] riggedHand = {"b1", "b1", "b1", "w2", "w2", "w2", "d3", "d4", "d5", "c6", "c6", "c6", "c1"};
//        riggedHands.add(Arrays.asList(riggedHand));
//
//        game = new Game(gameManager.getRandom(), riggedHands);
        game = new Game(gameManager.getRandom());
        new GameView(game, graphicEntityModule, tooltipModule);

        for (Player player : gameManager.getActivePlayers()) {
            game.initPlayer(player.getIndex(), player);
        }
    }

    private void setWinner(Player player) {
        setScore(player, 1);
    }

    private void setScore(Player player, int score) {
        gameManager.addToGameSummary(GameManager.formatSuccessMessage(player.getNicknameToken() + " won!"));
        player.setScore(score);
    }

    private void setLoser(Player player, String message) {
        gameManager.addToGameSummary(GameManager.formatSuccessMessage(message));
        player.setScore(-1);
    }

    private boolean attemptInterrupt() {
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
                        actions.add(Action.parse(outputs.get(0), player.getIndex(), game.getHands(), lastMeaningfulAction.player, lastDiscardedTile, true));
                    } catch (TimeoutException | InvalidAction e) {
                        setLoser(player, e.getMessage());
                        gameManager.endGame();
                        return true;
                    }
                }
            }

            System.out.println(actions);

            if (actions.size() > 0) { // Interrupting is an option for at least one person
                Action action = Collections.max(actions, Comparator.comparing(Action::getPriority));

                if (action.type.equals(Action.ActionType.Win)) {
                    for (Action a : actions) {
                        if (a.type.equals(Action.ActionType.Win))
                            setScore(gameManager.getPlayer(a.player), 1);
                    }

                    setLoser(gameManager.getPlayer(lastMeaningfulAction.player), "");
                    gameManager.endGame();
                } else if (!action.type.equals(Action.ActionType.Pass)) {
                    game.commitAction(action, 0.5);
                } else {
                    alreadyPassed = true;
                }

                return true;
            }
        }

        return false;
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

        if (!alreadyPassed && attemptInterrupt()) return;

        Player player = gameManager.getPlayer(game.getNextPlayer());
        System.out.printf("Turn of %d%n", player.getIndex());

        Action drawAction = new Action(player.getIndex(), Action.ActionType.Draw, new ArrayList<>());

        try {
            game.commitAction(drawAction, 0.33);
        } catch (IllegalArgumentException e) {
            gameManager.addToGameSummary("No more tiles, it's a draw!");
            gameManager.endGame();
            return;
        }

        ArrayList<Action> unknownActions = game.getUnknownActions(player.getIndex());

        player.sendInputLine("normal");
        player.sendInputLine(String.valueOf(unknownActions.size()));

        for (Action action : unknownActions) {
            player.sendInputLine(action.toString());
        }

        player.execute();

        try {
            List<String> outputs = player.getOutputs();
            Action action = Action.parse(outputs.get(0), player.getIndex(), game.getHands(), -1, drawAction.targets.get(0), false);

            if (action.type.equals(Action.ActionType.Win)) {
                player.setScore(1);
                gameManager.addToGameSummary(String.format("$%d wins!", player.getIndex()));
            }

            game.commitAction(action, 0.66);
        } catch (TimeoutException | InvalidAction e) {
            setLoser(player, e.toString());
            gameManager.endGame();
        }

        alreadyPassed = false;
    }

    @Override
    public void onEnd() {
        int[] scores = gameManager.getPlayers().stream().mapToInt(AbstractMultiplayerPlayer::getScore).toArray();
        String[] texts = new String[2];
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == 10) {
                texts[i] = "Sik wu!";
            }
        }
        endScreenModule.setScores(scores, texts);
//        endScreenModule.setTitleRankingsSprite(game.hands.get());;
    }
}
