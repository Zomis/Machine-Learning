package net.zomis.machlearn.text.duga;

import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PrecisionRecallF1;
import net.zomis.machlearn.images.MyGroovyUtils;
import net.zomis.machlearn.regression.ConvergenceIterations;
import net.zomis.machlearn.regression.GradientDescent;
import net.zomis.machlearn.regression.LogisticRegression;
import net.zomis.machlearn.text.BagOfWords;
import net.zomis.machlearn.text.TextFeatureBuilder;
import net.zomis.machlearn.text.TextFeatureMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ProgrammersCommentTest {

    @Test
    public void commentLearning() {
        String source = MyGroovyUtils.text(getClass().getClassLoader()
            .getResource("trainingset-programmers-comments.txt"));
        String[] lines = source.split("\n");
        BagOfWords bowYes = new BagOfWords();
        BagOfWords bowNo  = new BagOfWords();
        BagOfWords bowAll = new BagOfWords();
        TextFeatureBuilder textFeatures = new TextFeatureBuilder();

        LearningDataSet data = new LearningDataSet();
        List<String> processedStrings = new ArrayList<>();
        for (String str : lines) {
            if (!str.startsWith("0 ") && !str.startsWith("1 ")) {
                continue;
            }
            boolean expected = str.startsWith("1");
            String text = str.substring(2);
            String processed = preprocess(text);
            char expectedChar = expected ? '1' : '0';
            processedStrings.add(expectedChar + processed);
            textFeatures.add(processed);
            BagOfWords bow = expected ? bowYes : bowNo;
            bow.addText(text);
            bowAll.addText(text);
            // println text
        }

        TextFeatureMapper mapper = textFeatures.mapper();

        for (String str : processedStrings) {
            boolean expectTrue = str.charAt(0) == '1';
            data.add(str, mapper.toFeatures(str), expectTrue ? 1 : 0);
        }

        data.getData().stream().forEach(System.out::println);

        double[] learnedTheta = GradientDescent.gradientDescent(
            LogisticRegression.costFunction(data.getXs(), data.getY()),
            new ConvergenceIterations(10000),
            new double[data.numFeaturesWithZero()], 0.01);
        PrecisionRecallF1 score = data.precisionRecallF1(learnedTheta, (theta, x) ->
            LogisticRegression.hypothesis(theta, x) >= 0.5);
        System.out.println(score);

        System.out.println(bowAll.getData());
        System.out.println("-------------");
        System.out.println(bowYes.getData());
        System.out.println("-------------");
        System.out.println(bowNo.getData());
        System.out.println("-------------");

//        List<Map.Entry<String, Integer>> stream = bowAll.getData().entrySet().stream()
//            .sorted(Comparator.comparing({it.value}))
//            .collect(Collectors.toList())
////        stream.forEach({System.out.println(it)})
//        println 'Count ' + stream.size()
    }

    private String preprocess(String text) {
        return text.toLowerCase();
    }

}
