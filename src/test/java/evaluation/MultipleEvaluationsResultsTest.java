package evaluation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MultipleEvaluationsResultsTest {

    @Test
    void testAddRunResult() {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults("A Classifier", "An Instance");

        Evaluation evaluation = mock(Evaluation.class);
        when(evaluation.precision(1)).thenReturn(1.0);
        when(evaluation.recall(1)).thenReturn(2.0);
        when(evaluation.fMeasure(1)).thenReturn(3.0);
        when(evaluation.matthewsCorrelationCoefficient(1)).thenReturn(4.0);
        when(evaluation.numTruePositives(1)).thenReturn(5.0);
        when(evaluation.numTrueNegatives(1)).thenReturn(6.0);
        when(evaluation.numFalsePositives(1)).thenReturn(7.0);
        when(evaluation.numFalseNegatives(1)).thenReturn(8.0);
        Prediction predictionMock = mock(Prediction.class);
        when(predictionMock.predicted()).thenReturn(9.0);
        Prediction predictionMock2 = mock(Prediction.class);
        when(predictionMock2.predicted()).thenReturn(10.0);
        when(evaluation.predictions()).thenReturn(Lists.newArrayList(predictionMock, predictionMock2));

        results.addRunResult(evaluation, 11.0);

        evaluation = mock(Evaluation.class);
        when(evaluation.precision(1)).thenReturn(2.0);
        when(evaluation.recall(1)).thenReturn(4.0);
        when(evaluation.fMeasure(1)).thenReturn(6.0);
        when(evaluation.matthewsCorrelationCoefficient(1)).thenReturn(8.0);
        when(evaluation.numTruePositives(1)).thenReturn(10.0);
        when(evaluation.numTrueNegatives(1)).thenReturn(12.0);
        when(evaluation.numFalsePositives(1)).thenReturn(14.0);
        when(evaluation.numFalseNegatives(1)).thenReturn(16.0);
        predictionMock = mock(Prediction.class);
        when(predictionMock.predicted()).thenReturn(18.0);
        predictionMock2 = mock(Prediction.class);
        when(predictionMock2.predicted()).thenReturn(20.0);
        when(evaluation.predictions()).thenReturn(Lists.newArrayList(predictionMock, predictionMock2));

        results.addRunResult(evaluation, 22.0);

        assertEquals(1.5, results.getAveragePrecision());
        assertEquals(3.0, results.getAverageRecall());
        assertEquals(4.5, results.getAverageFMeasure());
        assertEquals(6.0, results.getAverageMCC());
        assertEquals(7.5, results.getAverageTruePositives());
        assertEquals(9.0, results.getAverageTrueNegatives());
        assertEquals(10.5, results.getAverageFalsePositives());
        assertEquals(12.0, results.getAverageFalseNegatives());
        assertIterableEquals(Lists.newArrayList(13.5, 15.0), results.getAveragePredictions());
        assertEquals(16.5, results.getAverageDiversity());
    }

    @Test
    void isEmpty() {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults("A Classifier", "An Instance");

        Prediction predictionMock = mock(Prediction.class);
        when(predictionMock.predicted()).thenReturn(9.0);
        Prediction predictionMock2 = mock(Prediction.class);
        when(predictionMock2.predicted()).thenReturn(10.0);


        assertTrue(results.isEmpty());
        results.addRunResult(mock(Evaluation.class));
        assertFalse(results.isEmpty());
    }
}