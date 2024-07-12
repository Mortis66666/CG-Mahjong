package Mahjong;

import Mahjong.view.GameView;
import com.codingame.game.Player;

import java.util.*;

public class Game {

    public ArrayList<Tile> pile;
    private final Random random;
    public ArrayList<Hand> hands;
    private GameView view;
    public ArrayList<Action> actions;
    private List<List<String>> riggedHands = new ArrayList<>();

    public Game(Random random) {
        this.random = random;

        actions = new ArrayList<Action>();

        makePile();
        makeHands();
    }

    public Game(Random random, List<List<String>> riggedHands) {
        // Debugging purpose
        this.random = random;
        this.riggedHands = riggedHands;

        actions = new ArrayList<Action>();

        makePile();
        makeHands();
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public void initPlayer(int playerId, Player player) {
        hands.get(playerId).view.drawHud(player);
    }

    public void commitAction(Action action, double time) {
        Hand hand = hands.get(action.player);
        Action lastAction = getLastMeaningfulAction();
        actions.add(action);

        switch (action.type) {
            case Discard:
                hand.discardTile(action.targets.get(0));
                break;
            case Draw:
                Tile drew = drawTile();
                hand.drawTile(drew);
                action.targets.add(drew);
                ArrayList<Tile> temp = new ArrayList<>();
                temp.add(drew);
                if (drew.isFlower()) {
                    commitAction(new Action(action.player, Action.ActionType.Flower, temp), -1);
                }
                break;
            case Flower:
                for (Tile s : action.targets) {
                    hand.doorify(s);
                }
                commitAction(new Action(action.player, Action.ActionType.Draw, new ArrayList<>()), -1);
                break;
            case Pong:
                Tile pongTarget = action.targets.get(0);
                hands.get(lastAction.player).freeDiscard(pongTarget);
                hand.drawTile(pongTarget);
                hand.pong(pongTarget);

                ArrayList<Tile> discard = new ArrayList<>(action.targets.subList(1, 2));
                action.targets.remove(1);
                commitAction(new Action(action.player, Action.ActionType.Discard, discard), -1);
                break;
            case Gong:
                Tile gongTarget = action.targets.get(0);
                hands.get(lastAction.player).freeDiscard(gongTarget);

                if (hand.countTiles(gongTarget) == 3) { // gong is from others
                    hand.drawTile(gongTarget);
                }

                hand.gong(gongTarget);
                break;
            case Seung:
                List<Tile> meld = action.targets.subList(0, 3);
                hands.get(lastAction.player).freeDiscard(meld.get(0));
                hand.drawTile(meld.get(0));

                hand.seung(meld);

                ArrayList<Tile> discardSeung = new ArrayList<>(action.targets.subList(3, 4));
                action.targets.remove(3);
                commitAction(new Action(action.player, Action.ActionType.Discard, discardSeung), -1);
                break;
        }


        if (time > 0)
            view.graphics.commitWorldState(time);
    }

    public Action getLastMeaningfulAction() {
        if (actions.size() == 0) return null;

        for (int i = actions.size() - 1; i > -1; i--) {
            Action action = actions.get(i);

            if (action.type.equals(Action.ActionType.Discard) || action.type.equals(Action.ActionType.Gong)) {
                return action;
            }
        }

        return null;
    }

    public ArrayList<Action> getUnknownActions(int playerId) {
        ArrayList<Action> result = new ArrayList<>();

        for (int i = actions.size() - 1; i > -1; i--) {
            Action action = actions.get(i);

            if (action.type.equals(Action.ActionType.Bookmark)) {
                if (action.player == playerId) {
                    break; // Break on bookmark
                } else {
                    continue; // Ignore on other's bookmark
                }
            }

            if (action.player != playerId && action.type.equals(Action.ActionType.Draw)) {
                continue;
            }

            result.add(0, action);
        }

        actions.add(new Action(playerId, Action.ActionType.Bookmark, new ArrayList<>()));
        return result;
    }

    public boolean canPlay(int playerId) {
        Action lastAction = getLastMeaningfulAction();

        if (lastAction == null) {
            return playerId == 0; // When there's no action previously, naturally only the first player can play
        }

        if (lastAction.type.equals(Action.ActionType.Gong)) {
            return playerId == lastAction.player; // When the last action is gong, the player
        }

        Hand hand = hands.get(playerId);


        int expectedNext = lastAction.player == 3 ? 0 : lastAction.player + 1;
        if (playerId == expectedNext) {
            return true;
        }

        return false;
    }

    public int getNextPlayer() {
        Action lastAction = getLastMeaningfulAction();
        if (lastAction == null) return 0;
        if (lastAction.type.equals(Action.ActionType.Gong)) return lastAction.player;
        return lastAction.player == 3 ? 0 : lastAction.player + 1;
    }

    public boolean canInterrupt(int playerId) {
        Hand hand = hands.get(playerId);
        Action action = getLastMeaningfulAction();

        if (action == null || action.type.equals(Action.ActionType.Gong) || action.type.equals(Action.ActionType.Pass)) {
            return false;
        }

        if (action.player == playerId) {
            return false;
        }

        Tile lastDiscarded = action.targets.get(action.targets.size() - 1);
        return hand.canInterrupt(lastDiscarded, action.player == (playerId == 0 ? 3 : playerId - 1));
    }

    private void makePile() {
        pile = new ArrayList<Tile>();

        for (int i = 0; i < 4; i++) {
            pile.add(new Tile(i + 1, Tile.TileType.Flower));
            pile.add(new Tile(i + 1, Tile.TileType.Season));

            for (int j = 1; j < 10; j++) {
                pile.add(new Tile(j, Tile.TileType.Character));
                pile.add(new Tile(j, Tile.TileType.Bamboo));
                pile.add(new Tile(j, Tile.TileType.Dot));


                if (j < 5)
                    pile.add(new Tile(j, Tile.TileType.Wind));

                if (j < 4)
                    pile.add(new Tile(j, Tile.TileType.Dragon));
            }
        }
    }

    private void makeHands() {
        int riggedAmount = riggedHands.size();

        hands = new ArrayList<>();

        for (List<String> strings : riggedHands) {
            ArrayList<Tile> handTiles = new ArrayList<Tile>();

            for (String riggedTile : strings) {
                handTiles.add(drawRiggedTile(riggedTile));
            }

            Hand hand = new Hand(handTiles);
            hands.add(hand);
        }

        for (int i = 0; i < 4 - riggedAmount; i++) {
            ArrayList<Tile> handTiles = new ArrayList<Tile>();

            for (int j = 0; j < 13; j++) {
                handTiles.add(drawTile());
            }

            Hand hand = new Hand(handTiles);
            hands.add(hand);
        }
    }

    private Tile drawRiggedTile(String tile) {
        for (int i = 0; i < pile.size(); i++) {
            if (Objects.equals(pile.get(i).toString(), tile)) {
                return pile.remove(i);
            }
        }

        return null;
    }

    public void checkFlowers() {
        for (int i = 0; i < 4; i++) {
            Hand hand = hands.get(i);

            for (ArrayList<Tile> flowers = hand.flowerTiles(); flowers.size() > 0; flowers = hand.flowerTiles()) {
                commitAction(new Action(i, Action.ActionType.Flower, flowers), 0.1);
            }
        }
    }

    public Tile drawTile() throws IllegalArgumentException {
        int randomIndex = random.nextInt(pile.size());
        return pile.remove(randomIndex);
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }
}
