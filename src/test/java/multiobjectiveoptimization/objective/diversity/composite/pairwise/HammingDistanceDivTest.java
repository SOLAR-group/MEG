package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HammingDistanceDivTest {
    HammingDistanceDiv hd = new HammingDistanceDiv();

    @Test
    void DivIsZeroWhenAllClassifiersIncorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new AlwaysIncorrectClassifier(),
        //                new AlwaysIncorrectClassifier(),
        //                new AlwaysIncorrectClassifier(),
        //        };
        //
        //        double div = hd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);
        Integer[] actuals = new Integer[]{1, 1, 0, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 0});

        double div = hd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsZeroWhenAllClassifiersCorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new AlwaysCorrectClassifier(),
        //                new AlwaysCorrectClassifier(),
        //                new AlwaysCorrectClassifier()
        //        };
        //
        //        double div = hd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);
        Integer[] actuals = new Integer[]{1, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0});

        double div = hd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsZeroWhenAllPairsOfClassifiersAgree() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0});
        //
        //        int[] preds = new int[]{0, 1, 1}; // wrong, correct, wrong
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(preds),
        //                new SometimesCorrectClassifier(preds),
        //                new SometimesCorrectClassifier(preds)
        //        };
        //
        //        double div = hd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);
        Integer[] actuals = new Integer[]{1, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1});

        double div = hd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsOneHalfWhenTwoClassifiersAgreeOnHalf() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 0, 1, 0, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 0, 0, 1, 0, 0}),
        //                new SometimesCorrectClassifier(new int[]{0, 1, 1, 1, 0, 0}),
        //        };
        //
        //        double div = hd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.5);
        Integer[] actuals = new Integer[]{1, 0, 0, 1, 0, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1, 0, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 1, 0, 0});

        double div = hd.calcDiversity(Lists.newArrayList(result1, result2));
        assertEquals(0.5, div);
    }

    @Test
    void DivWorksAsExpectedInOtherCases() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 0, 1, 0, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 0, 1, 1, 1, 0}),
        //                new SometimesCorrectClassifier(new int[]{1, 1, 0, 1, 0, 1}),
        //                new AlwaysCorrectClassifier(),
        //        };
        //
        //        double div = hd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 4 / (double) 9);
        Integer[] actuals = new Integer[]{1, 0, 0, 1, 0, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1, 0, 0});

        double div = hd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(4 / (double) 9, div);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(hd.convertDiversityToFitness(0.2), 5);
    }

    @Test
    void FitnessFunctionIsMaxWhenDivIsZero() {
        assertEquals(hd.convertDiversityToFitness(0), Double.MAX_VALUE);
    }
}
