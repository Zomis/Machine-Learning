package net.zomis.neuralone

class NeuronConnection implements NeuronLink {

    Neuron from
    Neuron to
    double weight = 1.0f

    double calculateInput() {
        return getInputValue() * getWeight()
    }

    @Override
    double getInputValue() {
        return from.output
    }

    static NeuronConnection create(Neuron input, Neuron output) {
        def conn = new NeuronConnection()
        conn.from = input
        conn.to = output
        conn
    }

    @Override
    String toString() {
        return "$from --> $to w($weight)"
    }
}
