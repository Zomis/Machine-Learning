package net.zomis.machlearn.neural

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.util.stream.Collectors

@RunWith(Parameterized)
class BasicPropagationTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<List<LearningData>> data = []
        data << Arrays.asList(
                new LearningData([0, 0] as double[], [0] as double[]),
                new LearningData([0, 1] as double[], [1] as double[]),
                new LearningData([1, 0] as double[], [1] as double[]),
                new LearningData([1, 1] as double[], [0] as double[]),
        )
        data << Arrays.asList(
                new LearningData([0, 0] as double[], [0] as double[]),
                new LearningData([0, 1] as double[], [0] as double[]),
                new LearningData([1, 0] as double[], [0] as double[]),
                new LearningData([1, 1] as double[], [1] as double[]),
        )
        data << Arrays.asList(
                new LearningData([0, 0] as double[], [0] as double[]),
                new LearningData([0, 1] as double[], [1] as double[]),
                new LearningData([1, 0] as double[], [1] as double[]),
                new LearningData([1, 1] as double[], [1] as double[]),
        )

        data.stream().map({[it] as Object[]}).collect(Collectors.toList())
    }

    @Parameterized.Parameter
    public List<LearningData> examples

    private NeuralNetwork network

    @Before
    void createNetwork() {
        network = new NeuralNetwork()
        def inputLayer = network.createLayer('IN')
        inputLayer.createNeuron()
        inputLayer.createNeuron()

        def middleLayer = network.createLayer('MIDDLE')
        middleLayer.createNeuron().addInputs(inputLayer)
        middleLayer.createNeuron().addInputs(inputLayer)

        def outputLayer = network.createLayer('OUT')
        outputLayer.createNeuron().addInputs(middleLayer)
    }

    @Test
    void learn() {
        new Backpropagation(0.2, 100000).backPropagationLearning(examples, network, new Random(42))

        network.printAll()

        for (LearningData data : examples) {
            def output = network.run(data.inputs)
            println "$data.inputs --> $output"
            network.printAll()
            println "-----------------"
            for (int i = 0; i < output.length; i++) {
                assert Math.abs(output[i] - data.outputs[i]) < 0.01
            }
        }
    }

}
