package net.zomis.machlearn.neural;

import org.apache.commons.math3.analysis.function.Sigmoid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Neuron {

    private static final Sigmoid sigmoid = new Sigmoid();
    public static final NeuronFunction SIGMOID_FUNCTION = x -> {
        double ePowerMinusX = Math.exp(-x);
        return 1 / (1 + ePowerMinusX);
        // (double) sigmoid.value(it)
    };
    private static final DoubleUnaryOperator derivative = x -> {
        // e^(-x)/(1+e^(-x))^2
//        double ePowerMinusX = Math.exp(-x)
//        double onePlusEPowerMinusX = 1 + ePowerMinusX
//        return ePowerMinusX / (onePlusEPowerMinusX * onePlusEPowerMinusX)
        double sigmoid = SIGMOID_FUNCTION.calculate(x);
        return sigmoid * (1 - sigmoid);
    };

    public final List<NeuronLink> inputs = new ArrayList<>();
    public final List<NeuronConnection> outputs = new ArrayList<>();
    private final NeuronFunction function = SIGMOID_FUNCTION;
    public double input;
    public double output;
    private final String name;
    final int indexInLayer;

    Neuron(int index, String name) {
        this.inputs.add(new DummyConnection());
        this.indexInLayer = index;
        this.name = name;
    }


    Neuron addInput(Neuron input) {
        NeuronConnection link = NeuronConnection.create(input, this);
        this.inputs.add(link);
        input.outputs.add(link);
        return this;
    }

    public double calculateInput() {
        double sum = 0;
        for (NeuronLink link : inputs) {
            sum += link.calculateInput();
        }
        return sum;
    }

    public double calculateOutput(double value) {
        return function.calculate(value);
    }

    public void addInputs(NeuronLayer layer) {
        for (Neuron neuron : layer) {
            addInput(neuron);
        }
    }

    double gPrimInput() {
        // derivative of function at input. g'(input)
        return derivative.applyAsDouble(input);
    }

    public void printInfo() {
        System.out.printf("Node %s\n inputs %s\n outputs %s\n input %f output %f%n",
            this, inputs, outputs, input, output);
    }

    @Override
    public String toString() {
        return name;
    }

    void process() {
        this.input = this.calculateInput();
        this.output = this.calculateOutput(this.input);
    }

    public List<NeuronLink> getInputs() {
        return inputs;
    }

    public double getOutput() {
        return output;
    }

    public List<NeuronConnection> getOutputs() {
        return outputs;
    }

    public void setOutput(double output) {
        this.output = output;
    }

}
