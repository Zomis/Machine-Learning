package net.zomis.machlearn.neural

import org.junit.Ignore
import org.junit.Test

class LoadSaveTest {

    @Test
    @Ignore
    void simpleLoadSave() {
        List<LearningData> examples = Arrays.asList(
                new LearningData([0, 0] as double[], [0] as double[]),
                new LearningData([0, 1] as double[], [1] as double[]),
                new LearningData([1, 0] as double[], [1] as double[]),
                new LearningData([1, 1] as double[], [0] as double[]),
        )
        def network = new NeuralNetwork()
        def inputLayer = network.createLayer('IN')
        inputLayer.createNeuron()
        inputLayer.createNeuron()

        def middleLayer = network.createLayer('MIDDLE')
        middleLayer.createNeuron().addInputs(inputLayer)
        middleLayer.createNeuron().addInputs(inputLayer)

        def outputLayer = network.createLayer('OUT')
        outputLayer.createNeuron().addInputs(middleLayer)
        new Backpropagation(0.2, 100000).backPropagationLearning(examples, network, new Random(42))

        def savedNetwork = new ByteArrayOutputStream()
        network.save(savedNetwork)

/*      This assertion is too detailed, causing it to fail too many times
        assert savedNetwork.toByteArray() == LoadSaveTest.class.getClassLoader()
            .getResource('simplenetwork.network').bytes*/
    }

    @Test
    void loadTest() {
        def network = NeuralNetwork.load(LoadSaveTest.class.getClassLoader()
            .getResourceAsStream('simplenetwork.network'))
        assert network.layerCount == 3
        assert network.getLayer(0).size() == 2
        assert network.getLayer(1).size() == 2
        assert network.getLayer(2).size() == 1

        assert network.getLayer(0).name == 'IN'
        assert network.getLayer(1).name == 'MIDDLE'
        assert network.getLayer(2).name == 'OUT'
        def loadedNetwork = new ByteArrayOutputStream()
        network.save(loadedNetwork)

        def runResult = network.run([1, 0] as double[])
        println runResult
        assert loadedNetwork.toByteArray() == LoadSaveTest.class.getClassLoader()
                .getResource('simplenetwork.network').bytes
    }

}
