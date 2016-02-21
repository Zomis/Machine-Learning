package net.zomis.gameai;

import net.zomis.gameai.features.IntegerFeature;

import java.util.Random;
import java.util.Scanner;

public class Game21LearnTest {

    private static final int MAX = 21;
    private static final int STEPS = 3;
    private static final int LEARN_FREQUENCY = 50;

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
    public void gamePlay() {
        GameAI idiot = new GameAI("Idiot");
        GameAI smartAI = new GameAI("SMART");
//        smartAI.addFeatureExtractor(Game21.class, "mod4", Integer.class, g -> g.getState() % 4);
        smartAI.addFeatureExtractor(Game21.class, new IntegerFeature<>("state", Game21::getState, 6, false));
        playMultiplayer(idiot, smartAI);
    }

    public void playMultiplayer(GameAI player1, GameAI player2) {
        WinsLosses winsLosses = new WinsLosses();
        WinsLosses all = new WinsLosses();
        for (int i = 1; i <= 10000; i++) {
            Random random = new Random(i);
            // play a game
            Game21 game21 = new Game21(MAX, STEPS, true);

            GameAI winner = playGame(game21, random, player1, player2);
            winsLosses.winResult(winner == player2);
            all.winResult(winner == player2);

            if (i % LEARN_FREQUENCY == 0) {
                System.out.println("Recent X: " + winsLosses + " ALL: " + all);
                if (random.nextDouble() >= winsLosses.getPercent()) {
                    player2.learn();
                }
                winsLosses.reset();
            }
        }
    }

    private GameAI playGame(Game21 game21, Random random, GameAI ai1, GameAI ai2) {
        GameMove[] moves = new GameMove[STEPS];
        for (int move = 0; move < moves.length; move++) {
            final int steps = move + 1;
            moves[move] = new GameMove(() -> game21.isMoveAllowed(steps), () -> game21.say(steps));
        }

        while (!game21.isFinished()) {
            GameAI currentAI = game21.getCurrentPlayer() == 0 ? ai1 : ai2;

/*
Game 21 --> max = 21, targetWins = true

Events      AI      Move      Network out
state 0     idiot   move 3    0.5
state 3     ai      move 2    0.55
state 5     idiot   move 1    0.42
state 6     ai      move 2    0.67
state 8     idiot   move 1    0.31
state 9     ai      move 3    0.79
state 12    idiot   move 3    0.23
state 15    ai      move 3    0.82
state 18    idiot   move 2    0
state 20    ai      move 1    1

state 21    idiot   ******    0 <--- this is useless information, I will never query it
state 21    ai      ******    1 <--- this is useless information, I will never query it

No need to calculate the average score, backpropagation will take care of that.
*/


            if (currentAI == ai2) {
                currentAI.inform(game21);
            }
            int previous = game21.getState();
            int bestMove = game21.getBestMove();
            currentAI.makeMove(random, moves);
            System.out.println(currentAI + " made move at " + previous + " is now " + game21.getState() +
              " Best move was " + bestMove);
        }
        boolean smartAIwin = game21.getWinner() == 1;
//            ai1.inform(game21);
//            ai2.inform(game21);
        ai1.endGameWithScore(smartAIwin ? 0 : 1);
        ai2.endGameWithScore(smartAIwin ? 1 : 0);
        return smartAIwin ? ai2 : ai1;
    }

    public static void main(String[] args) {
        HumanPlayer human = new HumanPlayer("Human", new Scanner(System.in));
        GameAI smartAI = new GameAI("SMART");
        smartAI.addFeatureExtractor(Game21.class, "state", Integer.class, Game21::getState);
//        smartAI.addFeatureExtractor(Game21.class, "mod4", Integer.class, g -> g.getState() % 4);
//        new Game21LearnTest().playMultiplayer(human, smartAI);
        new Game21LearnTest().gamePlay();

    }

}
