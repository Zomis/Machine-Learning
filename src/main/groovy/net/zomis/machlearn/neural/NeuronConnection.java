package net.zomis.machlearn.neural;

public class NeuronConnection implements NeuronLink {

    Neuron from;
    Neuron to;
    double weight = 1.0f;

    public double calculateInput() {
        return getInputValue() * getWeight();
    }

    @Override
    public double getInputValue() {
        return from.output;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double value) {
        this.weight = value;
    }

    public static NeuronConnection create(Neuron input, Neuron output) {
        NeuronConnection conn = new NeuronConnection();
        conn.from = input;
        conn.to = output;
        return conn;
    }

    @Override
    public String toString() {
        return "$from --> $to w($weight)";
    }

    public Neuron getTo() {
        return to;
    }

}
