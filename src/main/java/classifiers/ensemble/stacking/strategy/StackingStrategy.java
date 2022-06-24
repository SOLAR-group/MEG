package classifiers.ensemble.stacking.strategy;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.List;

public interface StackingStrategy {
    List<String[]> getAllCombinations();
    String getName();
}
