package net.zomis.neuralone

import org.apache.commons.math3.analysis.function.Sigmoid

import java.util.function.DoubleUnaryOperator;

class Neuron {

    private static final Sigmoid sigmoid = new Sigmoid()
    private static final DoubleUnaryOperator derivative = {x ->
        // e^(-x)/(1+e^(-x))^2
        double ePowerMinusX = Math.exp(-x)
        double onePlusEPowerMinusX = 1 + ePowerMinusX
        return ePowerMinusX / (onePlusEPowerMinusX * onePlusEPowerMinusX)
    }
    public static final NeuronFunction SIGMOID_FUNCTION = {x ->
        double ePowerMinusX = Math.exp(-x)
        return 1 / ePowerMinusX
        // (double) sigmoid.value(it)
    }

    List<NeuronConnection> inputs = []
    List<NeuronConnection> outputs = []
    NeuronFunction function = SIGMOID_FUNCTION
    double weight0
    double input
    double output
    String name

    Neuron(String name) {
        this.name = name
    }


    Neuron addInput(Neuron input) {
        def link = NeuronConnection.create(input, this)
        this.inputs << link
        input.outputs << link
        this
    }

    double calculateInput() {
        weight0 + inputs.stream().mapToDouble({it.calculateInput()}).sum()
    }

    double calculateOutput(double value) {
        return function.calculate(value)
    }

    void addInputs(NeuronLayer layer) {
        for (Neuron neuron : layer) {
            addInput(neuron)
        }
    }

    double gPrimInput() {
        // derivative of function at input. g'(input)
        return sigmoid.derivative().value(input)
    }

    void printInfo() {
        println "Node $this inputs $inputs outputs $outputs weight0 $weight0 input $input output $output"
    }

    @Override
    String toString() {
        name
    }
}
