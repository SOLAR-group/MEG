package multiobjectiveoptimization.objective.diversity.builder;

import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import multiobjectiveoptimization.objective.diversity.composite.nonpairwise.WAD;
import multiobjectiveoptimization.objective.diversity.composite.pairwise.*;

public class DiversityStrategyFactory {

    public static EnsembleDiversityStrategy buildStrategy(ClassificationObjective strategy) {
        return switch (strategy) {
            case DISAGREEMENT -> new Disagreement();
            case DOUBLE_FAULT -> new DoubleFault();
            case Q_STATISTIC -> new QStatistic();
            case CORRELATION -> new CorrelationCoefficient();
            case HAMMING -> new HammingDistanceDiv();
            case WAD -> new WAD();
            default -> null;
        };
    }
}
