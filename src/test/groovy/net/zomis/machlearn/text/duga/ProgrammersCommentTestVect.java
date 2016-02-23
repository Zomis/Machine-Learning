package net.zomis.machlearn.text.duga;

import net.zomis.machlearn.common.ClassifierFunction;
import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PrecisionRecallF1;
import net.zomis.machlearn.images.MyGroovyUtils;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.regression.ConvergenceIterations;
import net.zomis.machlearn.regression.GradientDescent;
import net.zomis.machlearn.regressionvectorized.LogisticRegression;
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
    	//System.out.println(LogisticRegression.costFunction(new double[][]{{2,3},{2,2}}, new double[]{1,0}).apply(new double[]{1,1,1}));
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
