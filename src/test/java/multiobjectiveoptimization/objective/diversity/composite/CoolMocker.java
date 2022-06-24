package multiobjectiveoptimization.objective.diversity.composite;

import com.google.common.collect.Streams;
import evaluation.EvaluationResult;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoolMocker {

    public static EvaluationResult mockResult(Integer[] actuals, Integer[] preds) {
        EvaluationResult result = mock(EvaluationResult.class);
        List<Prediction> predictions = Streams.zip(
                        Arrays.stream(actuals),
                        Arrays.stream(preds),
                        (actual, predicted) -> (Prediction) new NumericPrediction(actual, predicted))
                .toList();
        when(result.getPredictions()).thenReturn(predictions);
        return result;
    }
}
