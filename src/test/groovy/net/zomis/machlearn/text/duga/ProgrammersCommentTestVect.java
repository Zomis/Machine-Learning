package net.zomis.machlearn.text.duga;

import net.zomis.machlearn.common.ClassifierFunction;
import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PrecisionRecallF1;
import net.zomis.machlearn.images.MyGroovyUtils;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.regression.ConvergenceIterations;
import net.zomis.machlearn.regressionvectorized.GradientDescent;
import net.zomis.machlearn.regressionvectorized.LogisticRegression;
import net.zomis.machlearn.regressionvectorized.ModelFunction;
import net.zomis.machlearn.text.BagOfWords;
import net.zomis.machlearn.text.TextFeatureBuilder;
import net.zomis.machlearn.text.TextFeatureMapper;

import org.jblas.DoubleMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ProgrammersCommentTestVect {
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

	        DoubleMatrix gd = GradientDescent.gradientDescent(
	        		new DoubleMatrix(data.getXs()), new DoubleMatrix(data.getY()),
	            new ConvergenceIterations(20000),
	            new double[data.numFeaturesWithZero()], 0.01);
	        double[] learnedTheta = gd.toArray();
	        
	        double cost = LogisticRegression.costFunction(data.getXs(), data.getY()).apply(learnedTheta);

	        ClassifierFunction function = (theta, x) ->
	                LogisticRegression.hypothesis(theta, x) >= 0.3;

	        PrecisionRecallF1 score = data.precisionRecallF1(learnedTheta, function);

	        System.out.println("False negatives:");
	        data.stream()
	            .filter(LearningData::getOutputBoolean)
	            .filter(d -> !function.classify(learnedTheta, d.getInputs()))
	            .forEach(d -> System.out.println(d.getForData()));
	        System.out.println(cost);
	        System.out.println(score);

	        System.out.println(bowAll.getData());
	        System.out.println("-------------");
	        System.out.println(bowYes.getData());
	        System.out.println("-------------");
	        System.out.println(bowNo.getData());
	        System.out.println("-------------");
	}


    @Test
    public void gradientDescent() {
    	double[] result = GradientDescent.gradientDescentOld(
                LogisticRegression.costFunctionOld(new double[][]{{2,3},{2,2}}, new double[]{1,0}),
                new ConvergenceIterations(20000),
                new double[3], 0.01);
    	System.out.println("result="+result);
    	
    	DoubleMatrix result2 = GradientDescent.gradientDescent(new DoubleMatrix(new double[][]{{2,3},{2,2}}), new DoubleMatrix(new double[]{1,0}),
                new ConvergenceIterations(3),
                new double[3], 0.01);
    	System.out.println("result="+result2);
    }
    
    @Test
    public void checksigmoid() {
    	// Check if sigmoid returns correct value (oracle used)
    	DoubleMatrix XT = new DoubleMatrix(new double[][]{{1},{2},{3}});
    	DoubleMatrix expected = new DoubleMatrix(new double[][]{{0.731059},{0.880797},{0.952574}});
    	assert LogisticRegression.sigmoid(XT) != expected;	
    }
    
    @Test
    public void checkCostFunction() {
    	// check if vectorized costfunction returns same value as old one.
    	assert LogisticRegression.costFunction(new double[][]{{2,3},{2,2}}, new double[]{1,0}).apply(new double[]{1,1,1}) ==
    			LogisticRegression.costFunctionOld(new double[][]{{2,3},{2,2}}, new double[]{1,0}).apply(new double[]{1,1,1});
    }

    private String preprocess(String text) {
        return text.toLowerCase();
    }

}
