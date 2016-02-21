package net.zomis.gameai;

public class WinsLosses {

    private int wins;
    private int losses;

    public void winResult(boolean win) {
        if (win) {
            wins++;
        } else {
            losses++;
        }
    }

    public int getTotal() {
        return wins + losses;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public double getPercent() {
        double total = getTotal();
        return wins / total;
    }

    public void reset() {
        this.wins = 0;
        this.losses = 0;
    }

    @Override
    public String toString() {
        return "WinsLosses{" +
            "wins=" + wins +
            ", losses=" + losses +
            ", total=" + getTotal() +
            ", percentage=" + getPercent() +
            '}';
    }
}
