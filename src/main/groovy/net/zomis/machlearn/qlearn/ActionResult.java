package net.zomis.machlearn.qlearn;

public class ActionResult<G, A> {

    private final G newState;
    private final double reward;

    public ActionResult(G newState, double reward) {
        this.newState = newState;
        this.reward = reward;
    }

    public G getNewState() {
        return newState;
    }

    public double getReward() {
        return reward;
    }

}
