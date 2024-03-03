import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.setSeed(2311604759057582817L);

        // Adds as many player as you need to test your game
        gameRunner.addAgent(Agent2.class);
        gameRunner.addAgent(Agent2.class);
        gameRunner.addAgent(Agent1.class);
        gameRunner.addAgent(Agent1.class);


        gameRunner.start();

        // Another way to add a player
        // gameRunner.addAgent("python3 /home/user/player.py");
        //        GameResult result = gameRunner.simulate();
        //
        //        System.out.println(result.outputs);
        //        System.out.println(result.errors);
    }
}
