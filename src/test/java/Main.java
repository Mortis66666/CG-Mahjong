import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

//        gameRunner.setSeed(2311604759057582817L);
        gameRunner.setSeed(-547096388761865928L); // gong test

        // Adds as many player as you need to test your game
        gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
        gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
        gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
        gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");

        gameRunner.start();

        // Another way to add a player
//        for (int i = 0; i < 100; i++) {
//            MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
//
//            gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
//            gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
//            gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
//            gameRunner.addAgent("python /Users/User/Desktop/CG-game/HK-Mahjong/config/Boss.py3");
//            GameResult result = gameRunner.simulate();
//
//            System.out.println(result.outputs);
//            System.out.println(result.errors);
//        }
    }
}
