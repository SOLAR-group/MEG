package classifiers;

import classifiers.ensemble.stacking.StackingClassifier;
import com.google.common.collect.ContiguousSet;
import evaluation.EvaluationResult;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.composite.pairwise.AbstractPairwiseEnsembleDiversityStrategy;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Stacking;
import java.util.*;

/**
 * This class encapsulates the result of evaluating 15 base classifiers on the measures in Table 2 of replication study
 * and ranking them in the descending order for consumption later
 */
public class BaseClfPerformanceResults {
    private List<Pair<String, Double>> clfMCCs;
    private List<Pair<String, Double>> clfPrecisions;
    private List<Pair<String, Double>> clfDiversities;
    private List<Pair<String, Double>> clfWADs;
    private String arffPath;
    private AbstractPairwiseEnsembleDiversityStrategy diversityStrategy;
    private boolean divShouldBeMinimized;

    public BaseClfPerformanceResults(String arffPath, AbstractPairwiseEnsembleDiversityStrategy diversityStrategy) {
        this.arffPath = arffPath;
        this.diversityStrategy = diversityStrategy;

        this.clfDiversities = new ArrayList<>();
        this.clfWADs = new ArrayList<>();
        this.clfMCCs = new ArrayList<>();
        this.clfPrecisions = new ArrayList<>();

        this.divShouldBeMinimized = Math.abs((diversityStrategy.convertDiversityToFitness(2.0) - 2.0)) < 0.000001d;
        System.out.println("Diversity strategy " + diversityStrategy + (divShouldBeMinimized ? " should" : " shouldn't") + " be minimized");
    }

    private List<EvaluationResult> evaluateMCCAndPrecision(String[] clfNames) throws Exception {
        List<EvaluationResult> baseClassifierResults = new ArrayList<>();

        for (String clfName : clfNames) {
            System.out.println(clfName);
            AbstractClassifier clf = BaseClassifiers.buildBClf(clfName);
            MultipleEvaluationsResults evalResult = ModelEvaluation.evaluateClassifierBootstrapping(clf, arffPath, 1);
            baseClassifierResults.add(evalResult.get(0)); // there's only 1 run
            clfMCCs.add(new ImmutablePair<>(clfName, evalResult.getAverageMCC()));
            clfPrecisions.add(new ImmutablePair<>(clfName, evalResult.getAveragePrecision()));
        }

        return baseClassifierResults;
    }

    private void evaluateDIV(String[] clfNames, List<EvaluationResult> baseClassifierResults) {
        Set<Integer> indices = new HashSet<>(ContiguousSet.closedOpen(0, clfNames.length));
        Set<Integer> indicesCopy = new HashSet<>();
        indicesCopy.addAll(indices);

        for (int i : indices) {
            double diversity = 0;

            indicesCopy.remove(i); // set contains all classifier indices excluding current
            for(int j : indicesCopy) {
                double div =  diversityStrategy.calcDiv_ij(baseClassifierResults.get(i), baseClassifierResults.get(j));
                diversity += div;
            }

            // store average diversity
            diversity /= (double) clfNames.length-1;
            clfDiversities.add(new ImmutablePair<>(clfNames[i], diversity));

            indicesCopy.add(i); // restore set to original
        }
    }

    private void evaluateWAD(String[] clfNames) throws Exception {
        for(int i = 0 ; i < clfNames.length; i++) {
            String meta = clfNames[i];
            System.out.println("META:" + meta);
            Classifier metaClf = BaseClassifiers.buildBClf(meta);
            Classifier[] stackedBClfs = StackingClassifier.getStackedBClfs(clfNames);
            Stacking stacker = StackingClassifier.getStacker(metaClf, stackedBClfs);

            MultipleEvaluationsResults evalResult = ModelEvaluation.evaluateClassifierBootstrapping(stacker, arffPath, 1);
            double precision = evalResult.getAveragePrecision();

            double diversity = clfDiversities.get(i).getValue();
            if (this.divShouldBeMinimized) {
                // convert div to 0-1 (and 2 for QStat and Correlation Coeff) where greater is better
                diversity = 1 - diversity;
            }

            if((0.5 * precision + 0.5 * diversity) == 0) {
                System.out.println("WAD is invalid for " + meta + " in project " + arffPath);
                clfWADs.add(new ImmutablePair<>(meta, Double.NEGATIVE_INFINITY));
                continue;
            }

            double WAD = (precision * diversity) / (0.5 * precision + 0.5 * diversity);

            if(Double.isNaN(WAD)) {
                System.out.println("WAD is invalid for " + meta + " in project " + arffPath);
                clfWADs.add(new ImmutablePair<>(meta, Double.NEGATIVE_INFINITY));
                continue;
            }


            clfWADs.add(new ImmutablePair<>(meta, WAD));
        }
    }

    /**
     * Evaluates each classifier on Precision, MCC, DIV (disagreement) and WAD measures
     * and stores the results such that they are in descending order of the score (for each measure)
     *
     * @param clfNames      An array of {@link String} of the 15 base classifiers
     * @throws Exception    if the name of the classifier is invalid, or something goes wrong during cross validation
     */
    public void evaluateAndRankClassifiers(String[] clfNames) throws  Exception {
        // mcc and precision
        List<EvaluationResult> baseClassifierResults = evaluateMCCAndPrecision(clfNames);
        System.out.println("Finished mcc and precision results for ranking");
        evaluateDIV(clfNames, baseClassifierResults);
        System.out.println("Finished div results for ranking");
        evaluateWAD(clfNames);
        System.out.println("Finished wad results for ranking");

        // sort them
        Comparator<Pair<String,Double>> pairComparator = Comparator.comparing(Pair::getRight, Comparator.reverseOrder());
        clfMCCs = clfMCCs.stream().sorted(pairComparator).toList();
        clfPrecisions = clfPrecisions.stream().sorted(pairComparator).toList();
        clfWADs = clfWADs.stream().sorted(pairComparator).toList();

        Comparator<Pair<String,Double>> naturalComp = Comparator.comparing(Pair::getRight);
        clfDiversities = clfDiversities.stream().sorted(this.divShouldBeMinimized ? naturalComp : pairComparator).toList();
    }

    /**
     * Returns the name of the classifier that performed the best for the given {@link ClassificationObjective}
     * @param objective     {@link ClassificationObjective} enum of the desired measure
     * @return              {@link String} name of the base classifier that performed the best
     */
    public String getNameOfBestClfFor(ClassificationObjective objective) {
        return getEvalResultsFor(objective).get(0).getKey();
    }

    /**
     * Returns the evaluation results of all classifiers for a given {@link ClassificationObjective}, sorted in descending order
     * @param objective     {@link ClassificationObjective} enum of the desired measure
     * @return              A {@link List} of {@link Pair<String, Double>} where each pair contains the classifier name
     *                      and corresponding value for the given {@link ClassificationObjective} enum
     */
    public List<Pair<String, Double>> getEvalResultsFor(ClassificationObjective objective) {
        return switch(objective) {
            case MCC -> clfMCCs;
            case PRECISION -> clfPrecisions;
            case DISAGREEMENT, DOUBLE_FAULT, Q_STATISTIC, CORRELATION, HAMMING -> clfDiversities;
            case WAD -> clfWADs;
            default -> throw new IllegalArgumentException("Not a valid objective!");
        };
    }

    public List<Pair<String, Double>> getPrecisionResults() {
        return clfPrecisions;
    }

    public List<Pair<String, Double>> getDIVResults() {
        return clfDiversities;
    }

    public List<Pair<String, Double>> getMCCResults() {
        return clfMCCs;
    }

    public List<Pair<String, Double>> getWADResults() {
        return clfWADs;
    }
}
