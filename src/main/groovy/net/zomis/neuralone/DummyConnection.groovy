package net.zomis.neuralone

class DummyConnection implements NeuronLink {

    private double weight

    @Override
    double calculateInput() {
        return inputValue * this.@weight
    }

    @Override
    double getInputValue() {
        return 1
    }

    @Override
    double getWeight() {
        return this.@weight
    }

    @Override
    void setWeight(double value) {
        this.@weight = value
    }

    @Override
    String toString() {
        return "w0 $weight"
    }

}
