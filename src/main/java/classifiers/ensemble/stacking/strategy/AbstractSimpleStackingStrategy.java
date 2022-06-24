package classifiers.ensemble.stacking.strategy;

import org.apache.commons.lang3.tuple.Pair;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractSimpleStackingStrategy implements StackingStrategy {
    protected List<Pair<String, Double>> nameAndScoreSortedDescending;

    public AbstractSimpleStackingStrategy(List<Pair<String, Double>> nameAndScoreSortedDescending) {
        this.nameAndScoreSortedDescending = nameAndScoreSortedDescending;
    }

    @Override
    public List<String[]> getAllCombinations() {
        List<String> clfNamesSorted = nameAndScoreSortedDescending.stream().map(Pair::getKey).toList();
        return IntStream.rangeClosed(2, nameAndScoreSortedDescending.size()).mapToObj(i -> clfNamesSorted.subList(0, i).toArray(String[]::new)).toList();
    }
}
