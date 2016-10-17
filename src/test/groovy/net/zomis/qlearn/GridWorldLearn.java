package net.zomis.qlearn;

import net.zomis.machlearn.qlearn.ActionResult;
import net.zomis.machlearn.qlearn.GameInterface;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.IntStream;

public class GridWorldLearn implements GameInterface<int[][], Integer> {

    private final Random random = new Random();
    public static final int WALL = 1;
    public static final int PLAYER = 2;
    public static final int GOAL = 4;
    public static final int PIT = 8;
    private static final int[] values = { WALL, PLAYER, GOAL, PIT };

    @Override
    public int[][] startGame() {
        return new int[][] {
            { 0, 0, 0, 0 },
            { 0, 0, GOAL, 0 },
            { 0, WALL, PIT, 0 },
            { PLAYER, 0, 0, 0 }
        };
    }

    @Override
    public boolean isInProgress(int[][] state) {
        return !Arrays.stream(state)
            .flatMapToInt(Arrays::stream)
            .anyMatch(i -> (i & (PLAYER | GOAL)) == (PLAYER | GOAL) ||
                    (i & (PLAYER | PIT)) == (PLAYER | PIT));
    }

    @Override
    public double[] getState(int[][] state) {
        return Arrays.stream(state)
            .flatMapToInt(Arrays::stream)
            .flatMap(this::booleanStream)
            .mapToDouble(i -> i).toArray();
    }

    private IntStream booleanStream(int i) {
        return Arrays.stream(values).map(v -> (i & v) == v ? 1 : 0);
    }

    @Override
    public double epsilon(int epoch, int totalEpochs, int actionsTakenInEpoch) {
        double epsilon = 1.0 - ((double)epoch / totalEpochs);
        return Math.max(0.1, epsilon);
    }

    @Override
    public Integer randomAction(int[][] state) {
        return random.nextInt(4);
    }

    @Override
    public Integer bestAction(int[][] state, double[] result) {
        return IntStream.range(0, result.length).boxed()
            .max(Comparator.comparingDouble(i -> result[i]))
            .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ActionResult<int[][], Integer> performAction(int[][] state, Integer action) {
        int dx = 0;
        int dy = 0;
        if (action == 0) dy = -1;
        if (action == 1) dy =  1;
        if (action == 2) dx = -1;
        if (action == 3) dx =  1;

        int[][] copy = new int[state.length][state[0].length];
        int px = 0;
        int py = 0;
        for (int yy = 0; yy < state.length; yy++) {
            for (int xx = 0; xx < state.length; xx++) {
                copy[yy][xx] = state[yy][xx];
                if ((state[yy][xx] & PLAYER) == PLAYER) {
                    px = xx;
                    py = yy;
                }
            }
        }

        int nx = px + dx;
        int ny = py + dy;
        int reward = -1;
        if (nx >= 0 && nx < copy.length) {
            if (ny >= 0 && ny < copy[nx].length) {
                if ((copy[ny][nx] & WALL) != WALL) {
                    copy[ny][nx] |= PLAYER;
                    copy[py][px] ^= PLAYER;
                }
                if ((copy[ny][nx] & PIT) == PIT) {
                    reward = -10;
                }
                if ((copy[ny][nx] & GOAL) == GOAL) {
                    reward = 1000;
                }
            }
        }

        return new ActionResult<>(copy, reward);
    }

    @Override
    public int getActionIndex(int[][] state, Integer action) {
        return action;
    }
}
