package net.zomis.gameai;

import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.neural.NeuralNetwork;

import java.util.*;
import java.util.function.Function;
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
        double[] moveDouble = new double[moves.length];
        int moveIndex = indexOf(moves, action);
        moveDouble[moveIndex] = 1;
        if (currentGame != null) {
            // Some AIs might make moves without having previously called `inform`
            currentGame.get(currentGame.size() - 1).expandX(moveDouble);
        }
        return action;
    }

    private int indexOf(GameMove[] moves, GameMove action) {
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] == action) {
                return i;
            }
        }
        return -1;
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
                values[i++] = value;
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
        if (currentGame == null) {
            return;
        }
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

        Collection<LearningData> data = featureValues.stream()
            .flatMap(List::stream)
            .map(td -> new LearningData(td.getX(), td.getY()))
            .collect(Collectors.toList());
        backprop.backPropagationLearning(data, nn, new Random(42));
        this.network = nn;
    }

    public <E, F> void addFeatureExtractor(Class<E> clazz, String name,
           Class<F> featureClass, Function<E, F> valueRetriever) {
        extractors.putIfAbsent(clazz, new FeatureExtractors<>(clazz));
        FeatureExtractors<E> fe = (FeatureExtractors<E>) extractors.get(clazz);
        if (featureClass == Integer.class) {
            fe.add(new FeatureExtractor<E>() {
                @Override
                public Feature<Integer> extract(E object) {
                    F featureValue = valueRetriever.apply(object);
                    int value = (Integer)featureValue;
                    FeatureFunction<Integer> ff = new FeatureFunction<Integer>() {
                        @Override
                        public double value(int index, Integer data) {
                            if (index == 0) {
                                return data;
                            }
                            int shiftLeft = index - 1;
                            int shiftedLeft = 1 << shiftLeft;
                            int v = data & shiftedLeft;
                            return v >> shiftLeft;
                        }
                    };

                    return new Feature<Integer>(9, name, value, ff);
                }
            });
        }
    }

}
