package net.zomis.machlearn.neural;

interface NeuronLink {

    double calculateInput();
    double getInputValue();
    double getWeight();
    void setWeight(double value);

}