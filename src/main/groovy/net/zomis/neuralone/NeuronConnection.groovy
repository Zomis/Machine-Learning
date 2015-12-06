package net.zomis.neuralone

class NeuronConnection {

    Neuron from
    Neuron to
    float weight = 1.0f

    float calculateInput() {
        from.output * weight
    }

    static NeuronConnection create(Neuron input, Neuron output) {
        def conn = new NeuronConnection()
        conn.from = input
        conn.to = output
    }
}
