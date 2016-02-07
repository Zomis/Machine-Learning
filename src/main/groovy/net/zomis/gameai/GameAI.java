package net.zomis.gameai;

import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.neural.NeuralNetwork;
import net.zomis.machlearn.neural.NeuronLayer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameAI {

    private final String name;
    private NeuralNetwork network;
    private final Map<Class<?>, FeatureExtractors<?>> extractors = new HashMap<>();

    // use a flatMap of these featureValues to train on
    private final List<List<TrainingData>> featureValues = new ArrayList<>();
    private final Random random = new Random(42);
    private List<TrainingData> currentGame;

    public GameAI(String name) {
        this.name = name;
    }

    public GameMove makeMove(Random random, GameMove[] moves) {
        if (network == null) {
            return makeRandomMove(random, moves);
        }
        GameMove bestMove = null;
        double bestScore = -1;
        int bestMoveIndex = 0;
        for (int i = 0; i < moves.length; i++) {
            GameMove move = moves[i];
            if (!move.isAllowed()) {
                continue;
            }
            double score = evaluateMove(moves, i);
            System.out.println("Score for move " + i + " is: " + score);
            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
                bestMoveIndex = i;
            }
        }
        if (bestMove == null) {
            throw new IllegalStateException("No valid moves found");
        }
        bestMove.perform();
        storeAction(moves.length, bestMoveIndex);
        return bestMove;
    }

    public double evaluateMove(GameMove[] moves, int index) {
        double[] oldX = this.currentGame.get(currentGame.size() - 1).getX();
        double[] data = new double[moves.length];
        data[index] = 1;
        double[] x = Arrays.copyOf(oldX, oldX.length + data.length);
        for (int i = oldX.length; i < x.length; i++) {
            x[i] = data[i - oldX.length]; // TODO: Use System.arraycopy()
        }
        double[] output = network.run(x);
        return output[0];
    }

    public GameMove makeRandomMove(Random random, GameMove[] moves) {
        List<GameMove> allowedMoves = Arrays.stream(moves)
            .filter(GameMove::isAllowed)
            .collect(Collectors.toList());
        if (allowedMoves.isEmpty()) {
            throw new IllegalStateException("No move allowed");
        }
        int index = random.nextInt(allowedMoves.size());
        GameMove action = moves[index];
        action.perform();
        int moveIndex = indexOf(moves, action);
        storeAction(moves.length, moveIndex);
        return action;
    }

    private void storeAction(int moveCount, int moveIndex) {
        double[] moveDouble = new double[moveCount];
        moveDouble[moveIndex] = 1;
        if (currentGame != null) {
            // Some AIs might make moves without having previously called `inform`
            currentGame.get(currentGame.size() - 1).expandX(moveDouble);
        }
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

        double min = 0.6;// Example indices:  0   1   2   3   4
        double max = 1.0;// Example weights: 0.6 0.7 0.8 0.9 1.0
        double increase = (max - min) / (currentGame.size() - 1);
        double[] y = { score };
        for (int i = 0; i < currentGame.size(); i++) {
            TrainingData data = currentGame.get(i);
            data.setY(y);
            double weight = i * increase + min;
            data.setWeight(weight);
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
        Backpropagation backprop = new Backpropagation(0.1, 100000);
//        backprop.setLogRate(200);

        Collection<LearningData> data = featureValues.stream()
            .flatMap(List::stream)
            .map(td -> new LearningData(td.getX(), td.getY()))
            .collect(Collectors.toList());
        int[] layers = nn.layers.stream().mapToInt(NeuronLayer::size).toArray();
        System.out.println("Learning using " + data.size() + " training examples. " +
                "Layer sizes are " + Arrays.toString(layers));

        Consumer<NeuralNetwork> initalization = Backpropagation.initializeRandom(random);
        backprop.backPropagationLearning(data, nn, initalization);
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
//                            if (index == 0) {
//                                return data;
//                            }
                            int minus = 0;
                            int shiftLeft = index - minus;
                            int shiftedLeft = 1 << shiftLeft;
                            int v = data & shiftedLeft;
                            return v >> shiftLeft;
                        }
                    };

                    return new Feature<Integer>(2, name, value, ff);
                }
            });
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
