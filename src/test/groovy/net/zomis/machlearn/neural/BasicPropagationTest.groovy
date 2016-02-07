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
        data << Arrays.asList(
                new LearningData([0, 0, 1, 0, 0] as double[], [1] as double[]), // mod 4 == 0, best move = 1
                new LearningData([0, 0, 0, 1, 0] as double[], [0] as double[]), // mod 4 == 0, best move = 1
                new LearningData([0, 0, 0, 0, 1] as double[], [0] as double[]), // mod 4 == 0, best move = 1
                new LearningData([1, 0, 1, 0, 0] as double[], [0] as double[]), // Nothing is possible
                new LearningData([1, 0, 0, 1, 0] as double[], [0] as double[]), // Nothing is possible
                new LearningData([1, 0, 0, 0, 1] as double[], [0] as double[]), // Nothing is possible
                new LearningData([0, 1, 1, 0, 0] as double[], [0] as double[]),
                new LearningData([0, 1, 0, 1, 0] as double[], [0] as double[]),
                new LearningData([0, 1, 0, 0, 1] as double[], [1] as double[]),
                new LearningData([1, 1, 1, 0, 0] as double[], [0] as double[]),
                new LearningData([1, 1, 0, 1, 0] as double[], [1] as double[]),
                new LearningData([1, 1, 0, 0, 1] as double[], [0] as double[])
        )

        data.stream().map({[it] as Object[]}).collect(Collectors.toList())
    }

    @Parameterized.Parameter
    public List<LearningData> examples

    private NeuralNetwork network

    @Before
    void createNetwork() {
        int inputs = examples.get(0).inputs.length
        int hidden = Math.ceil(inputs / 2d) + 1
        int outputs = examples.get(0).outputs.length
        network = NeuralNetwork.createNetwork(inputs, hidden, outputs)
    }

    @Test
    void learn() {
        new Backpropagation(0.1, 100000).backPropagationLearning(examples, network,
            Backpropagation.initializeRandom(new Random(43)))

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
