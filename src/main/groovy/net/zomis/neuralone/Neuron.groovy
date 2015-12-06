package net.zomis.neuralone

import org.apache.commons.math3.analysis.function.Sigmoid;

class Neuron {

    private static final Sigmoid sigmoid = new Sigmoid()
    public static final NeuronFunction SIGMOID_FUNCTION = {
        it -> (float) sigmoid.value(it)
    }

    List<NeuronConnection> inputs = []
    List<NeuronConnection> outputs = []
    NeuronFunction function = SIGMOID_FUNCTION
    float weight0
    float input
    float output


    Neuron addOutput(Neuron out) {
        outputs << NeuronConnection.create(this, out)
        this
    }

    float calculateInput() {
        weight0 + inputs.stream().mapToDouble({it.calculateInput()}).sum()
    }

    float calculateOutput(float value) {
        return function.calculate(value)
    }
}
