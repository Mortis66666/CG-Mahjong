package Mahjong;

import com.codingame.game.Player;

public class Action {
    private int player;
    private ActionType type;
    private String target = "_";

    public enum ActionType {
        Draw, Discard, Pong, Seung, Gong
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
