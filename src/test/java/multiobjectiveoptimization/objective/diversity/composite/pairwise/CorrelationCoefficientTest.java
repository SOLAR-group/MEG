package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CorrelationCoefficientTest {
    // range = [-1, 1] theoretically, but depends on number L of classifiers (as with most other measures included)

    protected MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
    CorrelationCoefficient coef = new CorrelationCoefficient();

    @Test
    void DivIsNotUndefinedAndIsPositiveWhenAllClassifiersAreCorrectOrWrongTogether() {
        //            AbstractList<Instance> dummyInstances = createInstances(new int[] {1,1,0,0,1});
        //
        //            int[] preds = new int[] {1,1,0,1,0};
        //            Classifier[] clfs = new Classifier[] {
        //                new SometimesCorrectClassifier(preds), // last 2 wrong
        //                new SometimesCorrectClassifier(preds),
        //                new SometimesCorrectClassifier(preds),
        //            };
        //
        //            double div = coef.calcDiversity(clfs, dummyInstances);
        //            BigDecimal divDec = new BigDecimal(div, mc);
        //            BigDecimal divTest = new BigDecimal(0.708, mc);
        //
        //            assertEquals(divDec, divTest);
        Integer[] actuals = new Integer[]{1, 1, 0, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1, 0});

        double div = coef.calcDiversity(Lists.newArrayList(result1, result2, result3));
        BigDecimal divDec = new BigDecimal(div, mc);
        BigDecimal divTest = new BigDecimal(0.708, mc);
        assertEquals(divDec, divTest);
    }

    @Test
    void DivPositiveWhenClassifiersArePositivelyCorrelated() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 0, 1});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 0, 0, 0, 0}), // correct, wrong, correct, correct, wrong
        //                new SometimesCorrectClassifier(new int[]{1, 0, 1, 0, 1}), // same as 1st clf but different result on 3rd and last
        //                new SometimesCorrectClassifier(new int[]{1, 0, 0, 1, 1}), // same as 1st clf but different result on 4th
        //        };
        //
        //        double div = coef.calcDiversity(clfs, dummyInstances);
        //        assertTrue(div > 0.0);
        //        assertEquals(div, 1 / (double) 6);
        Integer[] actuals = new Integer[]{1, 1, 0, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 0, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1, 1});

        double div = coef.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertTrue(div > 0.0);
        assertEquals(1 / (double) 6, div);
    }

    @Test
    void DivNegativeWhenClassifiersAreNegativelyCorrelated() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 0, 1, 1});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 1, 1, 1, 0, 0}), // clf1 classifies correctly 1st and 2nd
        //                new SometimesCorrectClassifier(new int[]{0, 0, 0, 0, 0, 0}), // clf2 classifies correctly 3rd and 4th
        //                new SometimesCorrectClassifier(new int[]{0, 0, 1, 1, 1, 1}), // clf3 classifies correctly 5th and 6th
        //        };
        //
        //        double div = coef.calcDiversity(clfs, dummyInstances);
        //        assertTrue(div < 0.0);
        //
        //        assertEquals(div, -1 / (double) 3);

        Integer[] actuals = new Integer[]{1, 1, 0, 0, 1, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1, 0, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 0, 0, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 1, 1});

        double div = coef.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertTrue(div < 0.0);
        assertEquals(-1 / (double) 3, div);
    }

    @Test
    void DivWorksAsExpectedInOtherCases() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 0, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{0, 1, 0, 1, 1, 0}), // correct indices: 1,2,4,5
        //                new SometimesCorrectClassifier(new int[]{0, 0, 1, 1, 0, 1}), // correct indices: none
        //                new SometimesCorrectClassifier(new int[]{1, 1, 0, 0, 1, 1}), // correct indices: 0,1,2,3,4
        //        };
        //
        //        // pairwise correlation between 1st and 2nd  -0.09759
        //        // "" between 1st and 3rd -0.1490711985
        //        // "" between 2nd and 3rd -0.2182178902
        //
        //        double div = coef.calcDiversity(clfs, dummyInstances);
        //        BigDecimal divDec = new BigDecimal(div, mc);
        //        BigDecimal divTest = new BigDecimal(-0.155, mc);
        //
        //        assertEquals(divDec, divTest);
        Integer[] actuals = new Integer[]{1, 1, 0, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 0, 1, 1});

        double div = coef.calcDiversity(Lists.newArrayList(result1, result2, result3));
        BigDecimal divDec = new BigDecimal(div, mc);
        BigDecimal divTest = new BigDecimal(-0.155, mc);
        assertEquals(divDec, divTest);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(coef.convertDiversityToFitness(0.3), 0.3);
    }

    @Test
    void FitnessFunctionIsNotMaxWhenDiversityIsZero() {
        assertEquals(coef.convertDiversityToFitness(0), 0);
    }
}
