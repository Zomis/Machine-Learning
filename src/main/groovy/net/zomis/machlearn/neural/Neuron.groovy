package net.zomis.machlearn.neural

import org.apache.commons.math3.analysis.function.Sigmoid

import java.util.function.DoubleUnaryOperator;

class Neuron {

    private static final Sigmoid sigmoid = new Sigmoid()
    private static final DoubleUnaryOperator derivative = {x ->
        // e^(-x)/(1+e^(-x))^2
//        double ePowerMinusX = Math.exp(-x)
//        double onePlusEPowerMinusX = 1 + ePowerMinusX
//        return ePowerMinusX / (onePlusEPowerMinusX * onePlusEPowerMinusX)
        double sigmoid = SIGMOID_FUNCTION.calculate(x)
        return sigmoid * (1 - sigmoid)
    }
    public static final NeuronFunction SIGMOID_FUNCTION = {x ->
        double ePowerMinusX = Math.exp(-x)
        return 1 / (1 + ePowerMinusX)
        // (double) sigmoid.value(it)
    }

    List<NeuronLink> inputs = [new DummyConnection()]
    List<NeuronConnection> outputs = []
    NeuronFunction function = SIGMOID_FUNCTION
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
        double sum = 0
        for (NeuronLink link : inputs) {
            sum += link.calculateInput()
        }
        return sum
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
        return derivative.applyAsDouble(input)
    }

    void printInfo() {
        println "Node $this\n inputs $inputs\n outputs $outputs\n input $input output $output"
    }

    @Override
    String toString() {
        name
    }

    void process() {
        this.@input = this.calculateInput()
        this.@output = this.calculateOutput(this.@input)
    }
}
