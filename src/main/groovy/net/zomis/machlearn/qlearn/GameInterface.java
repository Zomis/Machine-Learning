package net.zomis.machlearn.qlearn;

public interface GameInterface<G, A> {

    G startGame();

    boolean isInProgress(G state);

    double[] getState(G state);

    double epsilon(int epoch, int totalEpochs, int actionsTakenInEpoch);

    A randomAction(G state);

    A bestAction(G state, double[] result);

    ActionResult<G,A> performAction(G state, A action);

    int getActionIndex(G state, A action);

}
