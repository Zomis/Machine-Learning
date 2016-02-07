package net.zomis.gameai;

public class Game21 {

    private final int max;
    private final int steps;
    private final boolean targetWins;
    private int state;
    private int currentPlayer;

    public Game21(int max, int steps, boolean targetWins) {
        this.max = max;
        this.steps = steps;
        this.targetWins = targetWins;
    }

    public int say(int steps) {
        if (isFinished()) {
            throw new IllegalStateException("Game over already.");
        }
        if (steps <= 0 || steps > this.steps) {
            throw new IllegalArgumentException("Action " + steps + " not allowed. Max is " + this.steps);
        }
        this.currentPlayer = (this.currentPlayer + 1) % 2;
        this.state += steps;
        if (state >= max) {
            state = max;
        }
        return state;
    }

    public int getSteps() {
        return steps;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getWinner() {
        if (state < max) {
            return -1;
        }
        return targetWins ? 1 - currentPlayer : currentPlayer;
    }

    public boolean isFinished() {
        return this.state == this.max;
    }

    public int getState() {
        return state;
    }

    public boolean isMoveAllowed(int steps) {
        return this.state + steps <= this.max;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBestMove() {
        // So far only adapted to if targetWins, and max == 21
        int mod = steps + 1;
        int stateMod = state % mod;
        int result = (mod - stateMod) % mod + 1;
        if (result == mod) {
            return 0;
        }
        return result;
    }

}
