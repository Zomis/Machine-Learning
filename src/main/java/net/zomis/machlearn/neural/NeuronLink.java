package net.zomis.machlearn.neural;

public interface NeuronLink {

    double calculateInput();
    double getInputValue();
    double getWeight();
    void setWeight(double value);

}