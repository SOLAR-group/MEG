package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntropyTest {
    // range: [0,1]
    Entropy entropy = new Entropy();

    @Test
    void DivIsZeroWhenAllClassifiersCorrectOnAllInstances() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1});
        //
        //        Classifier[] clfs = new Classifier[] {
        //            new AlwaysCorrectClassifier(),
        //            new AlwaysCorrectClassifier(),
        //            new AlwaysCorrectClassifier(),
        //        };
        //
        //        double div = entropy.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);

        Integer[] actuals = new Integer[]{1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});

        double div = entropy.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsZeroWhenAllClassifiersWrongOnAllInstances() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1});
        //
        //        Classifier[] clfs = new Classifier[] {
        //            new AlwaysIncorrectClassifier(),
        //            new AlwaysIncorrectClassifier(),
        //            new AlwaysIncorrectClassifier(),
        //        };
        //
        //        double div = entropy.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);

        Integer[] actuals = new Integer[]{1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});

        double div = entropy.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsOneWhenRoughlyHalfOfClassifiersAgreeOnEveryInstance() throws Exception {
        //        // agree here means either getting it wrong or correct together
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1,0});
        //
        //        // Case 1: Odd number of classifiers in ensemble, so max diversity is reached when ceil(3/2) = 1 classifier
        //        // makes a mistake on each instance
        //        Classifier[] clfsOdd = new Classifier[] {
        //                new SometimesCorrectClassifier(new int[]{0,0,1,1}), // first clf makes mistake on 1st and last instances
        //                new SometimesCorrectClassifier(new int[]{1,1,1,0}), // 2nd makes mistake on 2nd instance only
        //                new SometimesCorrectClassifier(new int[]{1,0,0,0}), // 3rd makes mistake on 3rd instance only
        //        };
        //
        //        double divOdd = entropy.calcDiversity(clfsOdd, dummyInstances);
        //        assertEquals(divOdd, 1.0);
        //
        //        // Case 1: Even number of classifiers in ensemble (L=4), so max diversity is reached when ceil(4/2) = 2 classifiers
        //        // i.e. half of them make a mistake on each instance
        //
        //        Classifier[] clfsEven = new Classifier[] {
        //                // on odd instance, last two clfs make mistakes,
        //                // on even instance, first two clfs make mistakes
        //                new SometimesCorrectClassifier(new int[]{1,1,1,1}),
        //                new SometimesCorrectClassifier(new int[]{1,1,1,1}),
        //                new SometimesCorrectClassifier(new int[]{0,0,0,0}),
        //                new SometimesCorrectClassifier(new int[]{0,0,0,0}),
        //        };
        //
        //        double divEven = entropy.calcDiversity(clfsEven, dummyInstances);
        //        assertEquals(divEven, 1.0);

        Integer[] actuals = new Integer[]{1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 0});

        double divOdd = entropy.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(1.0, divOdd);

        result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1});
        result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1});
        result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 0});
        EvaluationResult result4 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 0});

        double divEven = entropy.calcDiversity(Lists.newArrayList(result1, result2, result3, result4));
        assertEquals(1.0, divEven);
    }

    @Test
    void DivLowerThanOneWhenMajorityOfClassifiersAgreeOnEveryInstance() throws Exception {
        //        // div is lower because it should be a more even split
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1,0});
        //
        //        // let 3 classifiers agree (wrong or correct) for each instance
        //        // so entropy should be
        //        Classifier[] clfsOdd = new Classifier[] {
        //                new SometimesCorrectClassifier(new int[]{1,1,1,0}),
        //                new SometimesCorrectClassifier(new int[]{1,1,0,1}),
        //                new SometimesCorrectClassifier(new int[]{1,1,0,1}),
        //                new SometimesCorrectClassifier(new int[]{0,0,0,1}),
        //        };
        //
        //        double divOdd = entropy.calcDiversity(clfsOdd, dummyInstances);
        //        assertEquals(divOdd, 0.5);

        Integer[] actuals = new Integer[]{1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1});
        EvaluationResult result4 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 1});

        double divOdd = entropy.calcDiversity(Lists.newArrayList(result1, result2, result3, result4));
        assertEquals(0.5, divOdd);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(entropy.convertDiversityToFitness(0.2), 5);
    }

    @Test
    void FitnessFunctionIsMaxWhenDivIsZero() {
        assertEquals(entropy.convertDiversityToFitness(0), Double.MAX_VALUE);
    }
}
