package net.zomis.qlearn;

import net.zomis.machlearn.qlearn.ActionResult;
import net.zomis.machlearn.qlearn.GameInterface;
import net.zomis.machlearn.qlearn.OnlineLearningNetwork;
import net.zomis.machlearn.qlearn.QLearning;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

public class GridWorldTest {

    private GridWorldLearn gwl = new GridWorldLearn();

    @Test
    public void neuralInput() {
        double[] input = gwl.getState(gwl.startGame());
        System.out.println(Arrays.toString(input));
        System.out.println(Arrays.stream(input).sum());
    }

    @Test
    public void gameEnded() {
        Assert.assertFalse(gwl.isInProgress(new int[][] {
                { 0, 0, 0, 0 },
                { 0, 0, 6, 0 },
                { 0, 1, 8, 0 },
                { 0, 0, 0, 0 }
        }));
    }

    public static void main(String[] args) {
        new GridWorldTest().test();
    }

    @Test
    public void test() {
        int[][] state = gwl.startGame();
        print("Start", state);
        for (int i = 0; i < 4; i++) {
            ActionResult<int[][], Integer> newState = gwl.performAction(state, i);
            print("After action " + i, newState.getNewState());
        }

        Supplier<GameInterface<int[][], Integer>> supplier = () -> gwl;
        OnlineLearningNetwork network = new ZomisOnlineNetwork(64, 64, 4);
        // OnlineLearningNetwork network = new DL4jOnlineNetwork(64, 64, 4);
        QLearning<int[][], Integer> learn = new QLearning<>(0.9, supplier, network);
        learn.learn(10);
        playDetailed(supplier, network);
    }

    private void playDetailed(Supplier<GameInterface<int[][], Integer>> supplier, OnlineLearningNetwork network) {
        GameInterface<int[][], Integer> game = supplier.get();
        int[][] state;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                state = placePlayer(x, y, game.startGame());
                double[] result = network.run(game.getState(state));
                System.out.println("player at " + x + ", " + y + ": " + Arrays.toString(result));
            }
        }

        state = game.startGame();
        System.out.println("New Game!");
        Scanner scanner = new Scanner(System.in);
        while (game.isInProgress(state)) {
            print("State", state);
            double[] result = network.run(game.getState(state));
            System.out.println("Result: " + Arrays.toString(result));
            Integer action = game.bestAction(state, result);
            ActionResult<int[][], Integer> actionResult = game.performAction(state, action);
            state = actionResult.getNewState();
            System.out.println("Reward " + actionResult.getReward());
            scanner.nextLine();
        }
    }

    private int[][] placePlayer(int x, int y, int[][] ints) {
        for (int yy = 0; yy < ints.length; yy++) {
            for (int xx = 0; xx < ints[yy].length; xx++) {
                if (xx == x && yy == y) {
                    ints[yy][xx] = GridWorldLearn.PLAYER;
                } else {
                    ints[yy][xx] &= 0xffff ^ GridWorldLearn.PLAYER;
                }
            }
        }
        return ints;
    }

    private void print(String header, int[][] state) {
        System.out.println("----------------");
        System.out.println(header);
        for (int yy = 0; yy < state.length; yy++) {
            for (int xx = 0; xx < state[yy].length; xx++) {
                System.out.print(state[yy][xx]);
            }
            System.out.println();
        }
        System.out.println();


    }

}
