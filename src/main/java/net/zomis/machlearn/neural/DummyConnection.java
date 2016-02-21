package net.zomis.machlearn.neural;

class DummyConnection implements NeuronLink {

    private double weight = 1;

    @Override
    public double calculateInput() {
        return getInputValue() * this.weight;
    }

    @Override
    public double getInputValue() {
        return 1;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double value) {
        this.weight = value;
    }

    @Override
    public String toString() {
        return "w0 " + weight;
    }

}
