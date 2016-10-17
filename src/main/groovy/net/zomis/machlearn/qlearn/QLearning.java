package net.zomis.machlearn.qlearn;

import net.zomis.machlearn.neural.LearningData;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Supplier;

public class QLearning<G, A> {

    private final Random random = new Random(42);
    private final double gamma;
    private final Supplier<GameInterface<G, A>> gameSupplier;
    private final OnlineLearningNetwork network;

    public QLearning(double gamma, Supplier<GameInterface<G, A>> gameSupplier, OnlineLearningNetwork network) {
        this.gamma = gamma;
        this.gameSupplier = gameSupplier;
        this.network = network;
    }

    public void learn(int epochs) {
        for (int i = 0; i < epochs; i++) {
            if (i % 10 == 0) {
                System.out.println("Learn " + i);
            }
            int t = 0;
            GameInterface<G, A> game = gameSupplier.get();
            G state = game.startGame();
            while (game.isInProgress(state)) {
                // We are in state S
                // Let's run our Q function on S to get Q values for all possible actions
                double[] input = game.getState(state);
                double[] result = network.run(input);
                A action;
                if (random.nextDouble() <= game.epsilon(i, epochs, t)) {
                    action = game.randomAction(state);
                    System.out.println("Random action: " + actStr(action));
                } else {
                    // choose best action from Q(s,a) values
                    action = game.bestAction(state, result);
                    System.out.println("Best action: " + actStr(action));
                }

                // Take action, observe new state S'
                ActionResult<G, A> actionResult = game.performAction(state, action);

                // Get max_Q(S',a)
                double[] result2 = network.run(game.getState(actionResult.getNewState()));
                double maxQ = Arrays.stream(result2).max().orElseThrow(NoSuchElementException::new);
                double target;
                if (game.isInProgress(actionResult.getNewState())) {
                    System.out.println("MaxQ = " + maxQ);
                    target = actionResult.getReward() + (gamma * maxQ);
                } else {
                    target = actionResult.getReward();
                }
                System.out.println("Reward at " + t + ": " + target);
                result[game.getActionIndex(state, action)] = target;

                LearningData data = createData(input, result);
                network.learn(data);

                t++;
                state = actionResult.getNewState();
            }

        }

    }

    private String actStr(A action) {
        int i = (Integer) action;
        switch (i) {
            case 0: return "UP";
            case 1: return "DOWN";
            case 2: return "LEFT";
            case 3: return "RIGHT";
        }
        return null;
    }

    private LearningData createData(double[] input, double[] result) {
        return new LearningData(input, result);
    }

    /*
1 Setup a for-loop to number of epochs
2 In the loop, setup while loop (while game is in progress)
3 Run Q network forward.
4 We're using an epsilon greedy implementation, so at time t with probability  ϵϵ  we will choose a random action. With probability  1−ϵ1−ϵ  we will choose the action associated with the highest Q value from our neural network.
5 Take action  aa  as determined in (4), observe new state  s′s′  and reward  rt+1rt+1
6 Run the network forward using  s′s′ . Store the highest Q value (maxQ).
7 Our target value to train the network is reward + (gamma * maxQ) where gamma is a parameter ( 0<=γ<=10<=γ<=1 ).
8 Given that we have 4 outputs and we only want to update/train the output associated with the action we just took, our target output vector is the same as the output vector from the first run, except we change the one output associated with our action to: reward + (gamma * maxQ)
9 Train the model on this 1 sample. Repeat process 2-9 */

}
