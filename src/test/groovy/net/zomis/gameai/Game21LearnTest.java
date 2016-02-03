package net.zomis.gameai;

import org.junit.Test;

import java.util.Random;

public class Game21LearnTest {

    private static final int MAX = 21;
    private static final int STEPS = 3;
/*
@AIKnown
@AIKnown(Class<KnownStrategy>)
@AIRelevant(difficulty = 4 ?)
@AIIrrelevant
@AISkill(...)

@AIRelevant public int nimSum()
@AIRelevant public int someFeature()

play some games, analyze, learn
play some games, analyze, learn
play some games, analyze, learn
...
Mixins!

combine with AIScorers?

How does AI decide where to play? Neural Network? AIScorers? Linear/Logistic regression?

On every call to `ai.inform(obj)`,
1. copy all accessible object state
2. calculate features from the state and store

TARGET: When state is 20, you should (or not) say 1 to get to 21
TARGET: When state is 19, you should (or not) say 2 to get to 21
TARGET: When state is 18, you should (or not) say 3 to get to 21

Calculate some expected win? (using logistic regression or Neural Network)
*/
    @Test
    public void gamePlay() {
        GameAI idiot = new GameAI();
        GameAI ai = new GameAI();
        for (int i = 0; i < 10000; i++) {
            Random random = new Random(i);
            // play a game
            Game21 game21 = new Game21(MAX, STEPS, true);
            Runnable[] moves = new Runnable[STEPS];
            for (int move = 0; move < moves.length; move++) {
                final int steps = move + 1;
                moves[move] = () -> game21.say(steps);
            }

            while (!game21.isFinished()) {
                GameAI currentAI = game21.getCurrentPlayer() == 0 ? idiot : ai;
                currentAI.makeMove(random, moves);
                if (currentAI == ai) {
                    currentAI.inform(game21);
                }
            }
            boolean smartAIwin = game21.getWinner() == 1;
            idiot.endGameWithScore(smartAIwin ? 0 : 1);
            ai.endGameWithScore(smartAIwin ? 1 : 0);
        }
    }

}
