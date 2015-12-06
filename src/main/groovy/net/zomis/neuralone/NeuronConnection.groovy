package net.zomis.neuralone

class NeuronConnection {

    Neuron from
    Neuron to
    double weight = 1.0f

    double calculateInput() {
        from.output * weight
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
