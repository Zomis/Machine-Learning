package net.zomis.gameai;

import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.neural.NeuralNetwork;

import java.util.*;
import java.util.stream.Collectors;

public class GameAI {

    private NeuralNetwork network;
    private final Map<Class<?>, FeatureExtractors<?>> extractors = new HashMap<>();

    // use a flatMap of these featureValues to train on
    private final List<List<TrainingData>> featureValues = new ArrayList<>();
    private List<TrainingData> currentGame;

    public GameAI() {
        
    }

    public GameMove makeMove(Random random, GameMove[] moves) {
        List<GameMove> allowedMoves = Arrays.stream(moves)
            .filter(GameMove::isAllowed)
            .collect(Collectors.toList());
        if (allowedMoves.isEmpty()) {
            throw new IllegalStateException("No move allowed");
        }
        int index = random.nextInt(allowedMoves.size());
        GameMove action = moves[index];
        action.perform();
        return action;
    }

    public <E> void inform(E object) {
        initializeCurrentGame();

        extractors.putIfAbsent(object.getClass(), new FeatureExtractors<>(object.getClass()));
        FeatureExtractors<E> featureExtractors = (FeatureExtractors<E>) extractors.get(object.getClass());
        List<Feature<?>> features = featureExtractors.extract(object);
        int size = features.stream().mapToInt(Feature::getSize).sum();
        double[] values = new double[size];

        int i = 0;
        for (Feature feature : features) {
            for (int f = 0; f < feature.getSize(); f++) {
                double value = feature.toDouble(f);
                values[i] = value;
            }
        }
        TrainingData data = new TrainingData(values);
        currentGame.add(data);
    }

    private void initializeCurrentGame() {
        if (currentGame != null) {
            return;
        }
        currentGame = new ArrayList<>();
    }

    public void endGameWithScore(int score) {
        double[] y = { score };
        for (TrainingData data : currentGame) {
            data.setY(y);
        }
        FeatureScaling.scale(currentGame);
        this.featureValues.add(currentGame);
        currentGame = null;
    }

    public void learn() {
        int inputs = featureValues.stream()
            .flatMap(List::stream)
            .mapToInt(td -> td.getX().length)
            .max().orElse(0);
        int hidden1 = (int) Math.ceil(inputs / 2d);
        int hidden2 = (int) Math.ceil(inputs / 3d);
        NeuralNetwork nn = NeuralNetwork.createNetwork(inputs, hidden1, hidden2, 1);
        Backpropagation backprop = new Backpropagation(0.1, 1000);
        backprop.setLogRate(10);

        Collection<LearningData> data = new ArrayList<>();
        featureValues.stream()
            .flatMap(List::stream)
            .map(td -> new LearningData(td.getX(), td.getY()))
            .collect(Collectors.toList());
        backprop.backPropagationLearning(data, network, new Random(42));
        this.network = nn;
    }

}
