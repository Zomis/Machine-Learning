package net.zomis.machlearn.text.duga;

import net.tuis.ubench.UBench;
import net.zomis.machlearn.common.ClassifierFunction;
import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PartitionedDataSet;
import net.zomis.machlearn.images.MyGroovyUtils;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.regression.ConvergenceIterations;
import net.zomis.machlearn.regressionvectorized.GradientDescent;
import net.zomis.machlearn.regressionvectorized.LogisticRegression;
import net.zomis.machlearn.text.TextClassification;
import net.zomis.machlearn.text.TextFeatureWeights;
import net.zomis.machlearn.text.TextFeatureBuilder;
import net.zomis.machlearn.text.TextFeatureMapper;

import org.jblas.DoubleMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class ProgrammersCommentTestVect {
	
	  private static final Pattern PROG_LINK = Pattern.compile(Pattern.quote("<a href=\"http") + "s?"
	            + Pattern.quote("://programmers.stackexchange.com")
	            + "(/|/help/.*)?" + Pattern.quote("\">"));
	  

	    @Test
	    public void commentLearning(){
	        String source = MyGroovyUtils.text(getClass().getClassLoader()
	            .getResource("trainingset-programmers-comments.txt"));
	        String[] lines = source.split("\n");
	        TextFeatureBuilder textFeatures = new TextFeatureBuilder(new int[]{1, 2}, this::filter);


	        TextFeatureMapper oldMapper = new TextFeatureMapper(
	                "better fit", "better suited", "better place",
	                "close", "off-topic", "design", "whiteboard", "this question", "this site",
	                "programmers.se", "help at", "place to ask", "migrate", "belong",
	                "instead", "the place for", "try programmers", "for programmers",
	                "on programmers", "at programmers", "to programmers");

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
	        }

	        TextFeatureMapper mapper = textFeatures.mapper(500);
	        /*System.out.println("Counts:");
	        textFeatures.getCounts().entrySet().stream()
	            .sorted(TextFeatureBuilder.SORT_BY_VALUE)
	            .forEach(System.out::println);
	        System.out.println();
	        System.out.println();*/
	        System.out.println("Mapper features:");
	        System.out.println(Arrays.toString(mapper.getFeatures()));

	        for (String str : processedStrings) {
	            boolean expectTrue = str.charAt(0) == '1';
	            data.add(str, mapper.toFeatures(str), expectTrue ? 1 : 0);
	        }

	        //System.out.println("Data is:");
	        //data.getData().stream().forEach(System.out::println);
	        
	        PartitionedDataSet partitionedData = data.partition(0.6, 0.2, 0.2, new Random(42));
	        LearningDataSet trainingSet = partitionedData.getTrainingSet();
	        LearningDataSet crossValidSet = partitionedData.getCrossValidationSet();
	        LearningDataSet testSet = partitionedData.getTestSet();

	        DoubleMatrix learnedT = GradientDescent.gradientDescent(
	            new DoubleMatrix(trainingSet.getXs()), new DoubleMatrix(trainingSet.getY()),
	            new ConvergenceIterations(20000),
	            new double[data.numFeaturesWithZero()], 0.01);
	        double[] learnedTheta = learnedT.toArray();
			TextFeatureWeights weights = new TextFeatureWeights(mapper.getFeatures(), learnedTheta);
			weights.getMapByValue().stream().forEach(System.out::println);

			double cost = LogisticRegression.costFunction(trainingSet.getXs(), trainingSet.getY()).apply(learnedTheta);
	        System.out.println("Training Set Cost: " + cost);
	        
	        double crossCost = LogisticRegression.costFunction(crossValidSet.getXs(), crossValidSet.getY()).apply(learnedTheta);
	        System.out.println("Validation Set Cost: " + crossCost);

	        ClassifierFunction function = (theta, x) ->
	                LogisticRegression.hypothesis(theta, x) >= 0.3;

	        System.out.println("ALL Score: " + data.precisionRecallF1(learnedTheta, function));
	        System.out.println("Training Score: " + trainingSet.precisionRecallF1(learnedTheta, function));
	        System.out.println("CrossVal Score: " + crossValidSet.precisionRecallF1(learnedTheta, function));
	        System.out.println("TestSet  Score: " + testSet.precisionRecallF1(learnedTheta, function));


	        System.out.println("False negatives:");
	        data.stream()
	            .filter(LearningData::getOutputBoolean)
	            .filter(d -> !function.classify(learnedTheta, d.getInputs()))
	            .forEach(d -> System.out.println(d.getForData()));

			TextClassification classification = new TextClassification(this::preprocess, mapper, learnedTheta, 0.4);
			String text = "I wrote an <a href=\"http://programmers.stackexchange.com/a/313903/60357\">answer on Programmers.SE</a> that touches on this point. In short, the GoF design patterns book literally includes example code that returns arbitrary objects. This has largely gone unnoticed because that code is in Smalltalk, using dynamic typing. The C++ example code has to use void because semantic restrictions of that language, not because a void return type is a central feature of the Visitor Pattern. In Java, we can use generics for non-void return types in a type-safe manner. —";

			System.out.println(classification.score(text));
		}

	    private boolean filter(String feature, Integer nGram) {
	        return feature.trim().length() > ((nGram > 1) ? 7 : 2);
	    }
		
	    
	  	//@Test
	  	public void performanceTest(){
	  		String source = MyGroovyUtils.text(getClass().getClassLoader()
		            .getResource("trainingset-programmers-comments.txt"));
		        String[] lines = source.split("\n");
		        TextFeatureBuilder textFeatures = new TextFeatureBuilder(new int[]{1, 2}, this::filter);

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
		        }

		        TextFeatureMapper mapper = textFeatures.mapper(50);
		        for (String str : processedStrings) {
		            boolean expectTrue = str.charAt(0) == '1';
		            data.add(str, mapper.toFeatures(str), expectTrue ? 1 : 0);
		        }

		        new UBench("Comparative Performance")
		          .addTask("Vectorized",() -> GradientDescent.gradientDescent(
				            new DoubleMatrix(data.getXs()), new DoubleMatrix(data.getY()),
				            new ConvergenceIterations(20000),
				            new double[data.numFeaturesWithZero()], 0.01))
		          .addTask("None-Vectorized",() -> GradientDescent.gradientDescentOld(
		                  LogisticRegression.costFunctionOld(data.getXs(), data.getY()),
		                  new ConvergenceIterations(20000),
		                  new double[data.numFeaturesWithZero()], 0.01))
		          .press(100)
		          .report("Comparing vectorization");
		        
		        
		        DoubleMatrix learnedT = GradientDescent.gradientDescent(
		            new DoubleMatrix(data.getXs()), new DoubleMatrix(data.getY()),
		            new ConvergenceIterations(20000),
		            new double[data.numFeaturesWithZero()], 0.01);
		        double[] learnedTheta = learnedT.toArray();
	  	}
	  

    @Test
    public void gradientDescentTest() {
    	double[][] x= {{2,3},{2,2}};
    	double[] y = {1,0};
    	
    	double[] resultOld = GradientDescent.gradientDescentOld(
    			//check if gradient descent ends up with same params and cost.
                LogisticRegression.costFunctionOld(x, y),
                new ConvergenceIterations(20000),
                new double[3], 0.01);
    	
    	DoubleMatrix resultNew = GradientDescent.gradientDescent(new DoubleMatrix(x), new DoubleMatrix(y),
                new ConvergenceIterations(20000),
                new double[3], 0.01);

    	assert roundDown4(LogisticRegression.costFunction(x, y).apply(resultNew.toArray())) == 
    			roundDown4(LogisticRegression.costFunction(x, y).apply(resultOld));
    	
    	for(int i = 0; i < resultOld.length; i++){
    		assert roundDown4(resultOld[i]) == roundDown4(resultNew.toArray()[i]);
    	}
    }
    
    
    
    public static double roundDown4(double d) {
        return (long) (d * 1e4) / 1e4;
    }
    
    @Test
    public void sigmoidTest() {
    	// Check if sigmoid returns correct value (oracle used)
    	DoubleMatrix XT = new DoubleMatrix(new double[][]{{1},{2},{3}});
    	DoubleMatrix expected = new DoubleMatrix(new double[][]{{0.731059},{0.880797},{0.952574}});
    	assert LogisticRegression.sigmoid(XT) != expected;	
    }
    
    @Test
    public void CostFunctionTest() {
    	// check if vectorized costfunction returns same value as old one.
    	assert LogisticRegression.costFunction(new double[][]{{2,3},{2,2}}, new double[]{1,0}).apply(new double[]{1,1,1}) ==
    			LogisticRegression.costFunctionOld(new double[][]{{2,3},{2,2}}, new double[]{1,0}).apply(new double[]{1,1,1});
    }

    private String preprocess(String text) {
    	text = text.toLowerCase();
        text = PROG_LINK.matcher(text).replaceAll("(link-to-programmers)");
        text = text.replaceAll("<a href=\"([^\"]+)\">", "$1 "); // Extract links
        text = text.replaceAll("<[^<>]+>", " "); // Remove HTML
        text = text.replaceAll("\\d+", "(number)");
        text = text.replaceAll("stack overflow", "stackoverflow");
        text = text.replaceAll("stack exchange", "stackexchange");
        text = text.replaceAll("programmers.stackexchange.com/q", "(progs-question) ");
        text = text.replaceAll("programmers.stackexchange.com/t", "(progs-tag) ");
        text = text.replaceAll("programmers.stackexchange.com/a", "(progs-answer) ");
        text = text.replaceAll("(http|https)://[^\\s]*", "(unclassified-httpaddr)");
        text = text.replaceAll("[\\.?!,]", " ");
        text = text.replaceAll("\\(number\\) (secs?|mins?) ago", "");

        text = text.replaceAll("i'm ", "i am ");
        text = text.replaceAll("we're ", "we are ");
        text = text.replaceAll("i've ", "i have ");
        text = text.replaceAll("you've ", "you have ");
        text = text.replaceAll("you're ", "you are ");
        text = text.replaceAll("i'll ", "i will ");       
        text = text.replaceAll("can't ", "can not ");
        text = text.replaceAll("won't ", "will not ");
        text = text.replaceAll("he's ", "he is ");
        text = text.replaceAll("she's ", "she is ");
        text = text.replaceAll("it's ", "it is ");
        text = text.replaceAll("she'll ", "she will ");       
        text = text.replaceAll("he'll ", "he will ");
        text = text.replaceAll("it'll ", "it will ");
        text = text.replaceAll("what's ", "what is ");
        text = text.replaceAll("who's ", "who is ");
        text = text.replaceAll("shouldn't ", "should not ");
        text = text.replaceAll("wouldn't ", "would not ");
        text = text.replaceAll("couldn't ", "could not ");
        text = text.replaceAll(" don't ", " do not ");
        return text.replace("\"", "");
    }
    
}
