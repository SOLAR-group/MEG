package multiobjectiveoptimization.objective.diversity;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NCLTest {

    @Test
    void computeNCL() {
        NCL ncl = new NCL();
        List<Double> ensemblePredictions = Lists.newArrayList(10D, 10D, 10D, 10D);
        List<Double> classifierPredictions = Lists.newArrayList(12D, 10D, 12D, 10D);
        List<List<Double>> otherClassifierPredictions = Lists.newArrayList(
                Lists.newArrayList(100D, 10D, 100D, 10D),
                Lists.newArrayList(1000D, 100D, 1000D, 100D)
        );
        assertEquals(4320D, ncl.computeNCL(ensemblePredictions, classifierPredictions, otherClassifierPredictions));
    }

    @Test
    void getSumOfOthersDifferences() {
        NCL ncl = new NCL();
        List<Double> ensemblePredictions = Lists.newArrayList(10D, 10D, 10D, 10D);
        List<List<Double>> otherClassifierPredictions = Lists.newArrayList(
                Lists.newArrayList(100D, 10D, 100D, 10D),
                Lists.newArrayList(1000D, 100D, 1000D, 100D)
        );
        assertEquals(1080D, ncl.getSumOfOthersDifferences(ensemblePredictions, otherClassifierPredictions, 0));
        assertEquals(90D, ncl.getSumOfOthersDifferences(ensemblePredictions, otherClassifierPredictions, 1));
        assertEquals(1080D, ncl.getSumOfOthersDifferences(ensemblePredictions, otherClassifierPredictions, 2));
        assertEquals(90D, ncl.getSumOfOthersDifferences(ensemblePredictions, otherClassifierPredictions, 3));
    }

    @Test
    void getDifference() {
        NCL ncl = new NCL();
        List<Double> ensemblePredictions = Lists.newArrayList(10D, 10D, 10D, 10D);
        List<Double> classifierPredictions = Lists.newArrayList(100D, 10D, 100D, 10D);
        assertEquals(90D, ncl.getDifference(ensemblePredictions, classifierPredictions, 0));
        assertEquals(0D, ncl.getDifference(ensemblePredictions, classifierPredictions, 1));
        assertEquals(90D, ncl.getDifference(ensemblePredictions, classifierPredictions, 2));
        assertEquals(0D, ncl.getDifference(ensemblePredictions, classifierPredictions, 3));
    }
}