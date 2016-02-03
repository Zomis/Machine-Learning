package net.zomis.gameai;

import java.util.Random;

public class GameAI {

    public void makeMove(Random random, Runnable[] moves) {
        int index = random.nextInt(moves.length);
        Runnable action = moves[index];
        action.run();
    }

    public void inform(Object object) {

    }

    public void endGameWithScore(int score) {

    }

}
