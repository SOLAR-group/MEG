package classifiers.ensemble.stacking;

import classifiers.AbstractExecutableClassifier;
import classifiers.BaseClassifiers;
import classifiers.ExperimentResultWriter;
import classifiers.ensemble.stacking.strategy.StackingStrategy;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import org.apache.commons.lang3.tuple.Pair;
import util.Utils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Stacking;
import weka.core.Instances;

import java.util.*;
import java.util.stream.DoubleStream;

public class StackingClassifier extends AbstractExecutableClassifier {
    private final StackingStrategy stackingStrategy;

    public StackingClassifier(StackingStrategy stackingStrategy, Instances trainingData, Instances testData) {
        super("Stacking_by_" + stackingStrategy.getName(), trainingData, testData);
        this.stackingStrategy = stackingStrategy;
        System.out.println("StackingClassifier class called");
    }

    public static Stacking getStacker(Classifier base, Classifier[] stackedBClfs) {
        Stacking stacker = new Stacking();
        stacker.setMetaClassifier(base);
        stacker.setClassifiers(stackedBClfs);
        return stacker;
    }

    public static Classifier[] getStackedBClfs(String[] clfsName) throws Exception {
        Classifier[] clfs = new Classifier[clfsName.length];
        for (int i = 0; i < clfsName.length; i++) {
            AbstractClassifier clf = BaseClassifiers.buildBClf(clfsName[i]);
            clfs[i] = clf;
        }
        return clfs;
    }

    private void evalStacking(AbstractClassifier metaClf, ExperimentResultWriter writer, int runs) throws Exception {
        Map<String, Double> avgMCC = new HashMap<>();
        Map<String, Double> avgPrecisionD = new HashMap<>();
        List<Double> mccBetweenRuns = new ArrayList<>();
        List<Double> precisionBetweenRuns = new ArrayList<>();

        String name = this + " with meta: " + getNameWithParameterFromClassifier(metaClf);

        writer.writeNewline("==========================================================");
        writer.writeNewline(name);
        writer.newline();

        List<String[]> bestCombinationsByLength = stackingStrategy.getAllCombinations();

        List<String[]> bestCombinationsPerRun = new ArrayList<>();

        for (int i = 0; i < runs; i++) {
            double maxMCC = Double.NEGATIVE_INFINITY;
            double maxPrecision = Double.NEGATIVE_INFINITY;
            String[] bestCombinationForThisRun = null;

            for (String[] combination : bestCombinationsByLength) {
                // make a stacker out of combination and evaluate
                Classifier[] stackedBClfs = StackingClassifier.getStackedBClfs(combination);
                Stacking stacker = StackingClassifier.getStacker(metaClf, stackedBClfs);
                MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierBootstrapping(stacker, trainingData, 1);

                double precision = results.getAveragePrecision();
                double mcc = results.getAverageMCC();

                String key = Arrays.toString(combination);

                avgPrecisionD.put(key, avgPrecisionD.getOrDefault(key, 0d) + precision);
                avgMCC.put(key, avgMCC.getOrDefault(key, 0d) + mcc);

                if (mcc > maxMCC) {
                    maxMCC = mcc;
                    bestCombinationForThisRun = combination;
                }

                if (precision > maxPrecision) {
                    maxPrecision = precision;
                }
            }

            assert (bestCombinationForThisRun != null);

            mccBetweenRuns.add(maxMCC);
            precisionBetweenRuns.add(maxPrecision);
            bestCombinationsPerRun.add(bestCombinationForThisRun);
        }

        assert (mccBetweenRuns.size() == runs);

        avgPrecisionD.replaceAll((key, value) -> value / (double) runs);

        avgMCC.replaceAll((key, value) -> value / (double) runs);

        List<Map.Entry<String, Double>> sortedAvgPrecisionDefective = Utils.getEntrySetListSortedByValDescending(avgPrecisionD);
        List<Map.Entry<String, Double>> sortedAvgMCC = Utils.getEntrySetListSortedByValDescending(avgMCC);

        writer.writeNewline("Combinations and precision (in descending order)");
        writer.writeMap(sortedAvgPrecisionDefective, false);

        writer.writeNewline("Combinations and MCC (in descending order)");
        writer.writeMap(sortedAvgMCC, false);

        writer.writeStats(mccBetweenRuns, "MCC");

        writer.writeNewline("========== BEST COMBINATIONS PER RUN (FOR MCC) =========");
        for (String[] combination : bestCombinationsPerRun) {
            writer.writeNewline(Arrays.toString(combination));
        }

        writer.writeStats(precisionBetweenRuns, "PRECISION");

        writer.writeNewline("============ TEST ===============");
        int i = 0;
        for (String[] bestCombination : bestCombinationsPerRun) {
            i++;
            writer.writeNewline("Run " + i);
            writer.writeNewline("Combination: " + Arrays.toString(bestCombination));
            Classifier[] stackedBClfs = StackingClassifier.getStackedBClfs(bestCombination);
            Stacking stacker = StackingClassifier.getStacker(metaClf, stackedBClfs);
            MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierNoCrossValidationDifferentInstances(stacker, trainingData, testData);
            writer.writeNewline("MCC: " + results.getAverageMCC());
            writer.newline();
        }

        ArrayList<Double> mccs = new ArrayList<>();
        for (String[] combination : bestCombinationsByLength) {
            Classifier[] stackedBClfs = StackingClassifier.getStackedBClfs(combination);
            Stacking stacker = StackingClassifier.getStacker(metaClf, stackedBClfs);
            MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierNoCrossValidationDifferentInstances(stacker, trainingData, testData);
            mccs.add(results.getAverageMCC());
        }
        writer.writeNewline("Average Test MCC: " + mccs.stream().mapToDouble(Double::doubleValue).average().orElseGet(() -> Double.NaN));
    }

    @Override
    public void runExperiment(Pair<AbstractClassifier, AbstractClassifier> bestClfs, ExperimentResultWriter writer, int runs) throws Exception {
        AbstractClassifier clfBestMCC = bestClfs.getLeft();
        AbstractClassifier clfBestDef = bestClfs.getRight();

        String clfBestMCCName = getNameWithParameterFromClassifier(clfBestMCC);
        String clfBestDefName = getNameWithParameterFromClassifier(clfBestDef);

        System.out.println(".............. Stacking Classifier ................");
        System.out.println("Using the following as Stacking meta classifiers: ");
        System.out.println(clfBestMCCName + " (Best MCC) ");
        System.out.println(clfBestDefName + " (Best Defective Precision)");

        writer.writeNewline("Stacking strategy used: " + stackingStrategy.getName());
        writer.newline();

        evalStacking(clfBestMCC, writer, runs);

        // avoid redundant work if best classifier by MCC and precision are the same
//        if (clfBestMCCName.equals(clfBestDefName)) {
//            return;
//        }

//        evalStacking(clfBestDef, writer, runs);
    }
}

