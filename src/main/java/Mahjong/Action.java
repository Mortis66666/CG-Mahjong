package Mahjong;

import com.codingame.game.Player;

public class Action {
    public final int player;
    public final ActionType type;
    public String target = "_";

    public enum ActionType {
        Draw, Discard, Pong, Seung, Gong, Flower
    }

    public Action(int committer, ActionType action, String target) {
        this.player = committer;
        this.type = action;
        this.target = target;
    }

    public String toString() {
        return String.format("%d %s %s", player, type.name(), target);
    }
}
