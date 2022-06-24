package classifiers;

import classifiers.ensemble.stacking.StackingClassifier;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import multiobjectiveoptimization.objective.diversity.composite.pairwise.AbstractPairwiseEnsembleDiversityStrategy;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import util.Utils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Stacking;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.*;

public class BaseClassifiers extends AbstractExecutableClassifier {
    public BaseClassifiers(Instances trainingData, Instances testData) {
        super("Base", trainingData, testData);
        System.out.println("BaseClassifiers class called");
    }

    public static NaiveBayes buildNB(String[] opts) throws Exception {
        NaiveBayes nb = new NaiveBayes();
        nb.setOptions(opts);
        return nb;
    }

    public static IBk buildKNN(String[] opts) throws Exception {
        IBk knn = new IBk();
        knn.setOptions(opts);
        return knn;
    }

    public static SMO buildSVM(String[] opts) throws Exception {
        SMO smo = new SMO();
        smo.setOptions(opts);
        return smo;
    }

    public static J48 buildTree(String[] opts) throws Exception {
        J48 tree = new J48();
        tree.setOptions(opts);
        return tree;
    }

    public static AbstractClassifier buildBClf(String clfName) throws Exception {
        String[] split = clfName.split("-");

        if (clfName.startsWith("NaiveBayes")) {
            // clfName looks like: NaiveBayes-K, NaiveBayes-D, or NaiveBayes
            if (split.length == 1) {
                // must be NaiveBayes (with default params);
                return buildNB(new String[]{});
            }

            return buildNB(new String[]{"-" + split[1]});
        }

        String param = split[1];

        if (clfName.startsWith("IBk")) {
            // clfName looks like IBk-K7
            return buildKNN(new String[]{"-K", param.substring(1)});
        }

        if (clfName.startsWith("SMO")) {
            // clfName looks like SMO-C10.0
            return buildSVM(new String[]{"-C", param.substring(1)});
        }

        if (clfName.startsWith("J48")) {
            // clfName looks like J48-C0.25
            return buildTree(new String[]{"-C", param.substring(1)});
        }

        return null;
    }

    public static BaseClfPerformanceResults getPerformances(String arffPath, AbstractPairwiseEnsembleDiversityStrategy diversityStrategy) throws Exception {
        BaseClfPerformanceResults results = new BaseClfPerformanceResults(arffPath, diversityStrategy);
        results.evaluateAndRankClassifiers(clfNames);
        return results;
    }

    @Override
    public void runExperiment(Pair<AbstractClassifier, AbstractClassifier> notused, ExperimentResultWriter writer, int runs) throws Exception {
        Map<String, List<Double>> precisionsByClf = new HashMap<>();
        Map<String, List<Double>> mccsByClf = new HashMap<>();

        List<Double> mccs = new ArrayList<>();
        List<Double> precisions = new ArrayList<>();

        System.out.println(".............. Base Classifiers ................");

        for (int i = 0; i < runs; i++) {
            double totalMCC = 0;
            double totalPrecision = 0;

            for (String clfName : clfNames) {
                AbstractClassifier clf = BaseClassifiers.buildBClf(clfName);
                MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierBootstrapping(clf, trainingData, 1);

                double precision = results.getAveragePrecision();
                double mcc = results.getAverageMCC();

                precisionsByClf.putIfAbsent(clfName, new ArrayList<>());
                mccsByClf.putIfAbsent(clfName, new ArrayList<>());
                precisionsByClf.get(clfName).add(precision);
                mccsByClf.get(clfName).add(mcc);

                totalMCC += mcc;
                totalPrecision += precision;
            }

            mccs.add(totalMCC / (double) clfNames.length);
            precisions.add(totalPrecision / (double) clfNames.length);
        }

        assert(precisionsByClf.size() == 14);
        assert(mccsByClf.size() == 14);

        Map<String, Pair<String, Double>> bestFourClassifiers = new HashMap<>();
        for(String clf : mccsByClf.keySet()) {
            String clfType="";
            if(clf.startsWith("NaiveBayes")) clfType = "NB";
            if(clf.startsWith("IBk")) clfType = "KNN";
            if(clf.startsWith("J48")) clfType = "TREE";
            if(clf.startsWith("SMO")) clfType = "SVM";

            double averageMCC = mccsByClf.get(clf).stream().mapToDouble(Double::doubleValue).average().orElse(Double.NEGATIVE_INFINITY);
            if(bestFourClassifiers.containsKey(clfType)) {
                if(averageMCC > bestFourClassifiers.get(clfType).getRight()) bestFourClassifiers.put(clfType, new ImmutablePair<>(clf, averageMCC));
            } else {
                bestFourClassifiers.put(clfType, new ImmutablePair<>(clf, averageMCC));
            }
        }

        Map<String, Double> avgPrecisionPerClf = new HashMap<>();
        precisionsByClf.forEach((k,v) -> avgPrecisionPerClf.put(k, v.stream().mapToDouble(Double::doubleValue).average().orElse(-100)));

        Map<String, Double> avgMCCPerClf = new HashMap<>();
        mccsByClf.forEach((k,v) -> avgMCCPerClf.put(k, v.stream().mapToDouble(Double::doubleValue).average().orElse(-100)));

        List<Map.Entry<String, Double>> sortedAvgPrecisionDefectiveByClf = Utils.getEntrySetListSortedByValDescending(avgPrecisionPerClf);
        List<Map.Entry<String, Double>> sortedAvgMCCByClf = Utils.getEntrySetListSortedByValDescending(avgMCCPerClf);

        writer.writeNewline("Precision for defective class: ");
        writer.writeMap(sortedAvgPrecisionDefectiveByClf, false);

        writer.writeNewline("MCC: ");
        writer.writeMap(sortedAvgMCCByClf, false);

        writer.writeStats(mccs, "MCC");
        writer.writeStats(precisions, "PRECISION");

        writer.newline();
        writer.writeNewline("STATS FOR 4 BEST CLASSIFIERS BY AVERAGE MCC OVER 30 SPLITS: ");

        writer.writeNewline("============ TEST ===============");

        ArrayList<Double> test_mccs = new ArrayList<>();
        for (Map.Entry<String, Pair<String, Double>> combination : bestFourClassifiers.entrySet()) {
            String classifierName = combination.getValue().getLeft();
            AbstractClassifier clf = BaseClassifiers.buildBClf(classifierName);
            MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierNoCrossValidationDifferentInstances(clf, trainingData, testData);
            test_mccs.add(results.getAverageMCC());
            writer.writeNewline(classifierName + ": " + results.getAverageMCC());
        }
        writer.writeNewline("Average Test MCC: " + test_mccs.stream().mapToDouble(Double::doubleValue).average().orElseGet(() -> Double.NaN));
    }
}
