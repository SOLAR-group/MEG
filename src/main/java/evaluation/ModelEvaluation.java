package evaluation;

import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import org.apache.commons.lang3.tuple.Pair;
import preprocessing.Preprocessing;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModelEvaluation {

    public static MultipleEvaluationsResults crossValidateClassifier(AbstractClassifier classifier,
                                                                     Instances data,
                                                                     int runs,
                                                                     int folds) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                data.relationName());
        for (int i = 0; i < runs; i++) {
            // Fix the seed for the randomisation of the data
            int seed = i + 1;
            Random rand = new Random(seed);
            Evaluation evalAll = new Evaluation(data);
            evalAll.crossValidateModel(classifier, data, folds, rand);

            results.addRunResult(evalAll);
        }
        return results;
    }

    public static MultipleEvaluationsResults crossValidateEnsembleClassifier(MultipleClassifiersCombiner ensemble,
                                                                             Instances data,
                                                                             int runs,
                                                                             int folds,
                                                                             EnsembleDiversityStrategy diversityStrategy,
                                                                             List<MultipleEvaluationsResults> baseCassifierResults) throws Exception {
        MultipleEvaluationsResults results = crossValidateClassifier(ensemble, data, runs, folds);
        // results is list of CV results, 1 per run
        // baseClassifierResult is list of <list of CV results>, 1 per classifier
        computeDiversity(runs, diversityStrategy, baseCassifierResults, results);
        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierNoCrossValidation(AbstractClassifier classifier,
                                                                                 String dataPath,
                                                                                 int runs) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                dataPath);
        Preprocessing preProcessing = new Preprocessing();
        for (int i = 0; i < runs; i++) {
            // Fix the seed for the randomisation of the data
            int seed = i + 1;
            Pair<Instances, Instances> trainTestData = preProcessing.getTrainTestData(dataPath, true, seed);
            Instances trainingData = trainTestData.getLeft();
            Instances testData = trainTestData.getRight();

            Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
            classifierCopy.buildClassifier(trainingData);

            Evaluation evalAll = new Evaluation(trainingData);
            evalAll.evaluateModel(classifierCopy, testData);

            results.addRunResult(evalAll);
        }
        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierNoCrossValidationDifferentInstances(AbstractClassifier classifier,
                                                                                                   String trainingPath,
                                                                                                   String testingPath) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                trainingPath);
        Preprocessing preProcessing = new Preprocessing();

        Instances trainingData = preProcessing.preprocessCrossVersionData(trainingPath);
        Instances testData = preProcessing.preprocessCrossVersionData(testingPath);

        Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
        classifierCopy.buildClassifier(trainingData);

        Evaluation evalAll = new Evaluation(trainingData);
        evalAll.evaluateModel(classifierCopy, testData);

        results.addRunResult(evalAll);

        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierNoCrossValidationDifferentInstances(AbstractClassifier classifier,
                                                                                                   Instances trainingData,
                                                                                                   Instances testData) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                trainingData.getRevision());

        Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
        classifierCopy.buildClassifier(trainingData);

        Evaluation evalAll = new Evaluation(trainingData);
        evalAll.evaluateModel(classifierCopy, testData);

        results.addRunResult(evalAll);

        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierBootstrapping(AbstractClassifier classifier,
                                                                             String dataPath,
                                                                             int runs) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                dataPath);
        Preprocessing preProcessing = new Preprocessing();
        for (int i = 0; i < runs; i++) {
            // Fix the seed for the randomisation of the data
            int seed = i + 1;
            Instances trainingData = preProcessing.preprocessCrossVersionData(dataPath);
            Instances testData = new Instances(trainingData);

            Resample resampler = new Resample();
            resampler.setRandomSeed(seed);
            resampler.setSampleSizePercent(80);
            resampler.setInputFormat(trainingData);
            trainingData = Filter.useFilter(trainingData, resampler);
            testData.removeAll(trainingData);

            Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
            classifierCopy.buildClassifier(trainingData);

            Evaluation evalAll = new Evaluation(trainingData);
            evalAll.evaluateModel(classifierCopy, testData);

            results.addRunResult(evalAll);
        }
        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierBootstrapping(AbstractClassifier classifier,
                                                                             Instances trainingInstances,
                                                                             int runs) throws Exception {
        MultipleEvaluationsResults results = new MultipleEvaluationsResults(
                classifier.getClass().getSimpleName() + Arrays.toString(classifier.getOptions()),
                trainingInstances.getRevision());
        Preprocessing preProcessing = new Preprocessing();
        for (int i = 0; i < runs; i++) {
            // Fix the seed for the randomisation of the data
            int seed = i + 1;
            Instances trainingData = new Instances(trainingInstances);
            Instances testData = new Instances(trainingData);

            Resample resampler = new Resample();
            resampler.setRandomSeed(seed);
            resampler.setSampleSizePercent(80);
            resampler.setInputFormat(trainingData);
            trainingData = Filter.useFilter(trainingData, resampler);
            testData.removeAll(trainingData);

            Classifier classifierCopy = AbstractClassifier.makeCopy(classifier);
            classifierCopy.buildClassifier(trainingData);

            Evaluation evalAll = new Evaluation(trainingData);
            evalAll.evaluateModel(classifierCopy, testData);

            results.addRunResult(evalAll);
        }
        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierBootstrapping(AbstractClassifier ensemble,
                                                                             Instances trainingInstances, int runs,
                                                                             EnsembleDiversityStrategy diversityStrategy,
                                                                             List<MultipleEvaluationsResults> baseCassifierResults) throws Exception {
        MultipleEvaluationsResults results = evaluateClassifierBootstrapping(ensemble, trainingInstances, runs);
        computeDiversity(runs, diversityStrategy, baseCassifierResults, results);
        return results;
    }

    public static MultipleEvaluationsResults evaluateClassifierBootstrapping(AbstractClassifier ensemble,
                                                                             String dataPath,
                                                                             int runs,
                                                                             EnsembleDiversityStrategy diversityStrategy,
                                                                             List<MultipleEvaluationsResults> baseCassifierResults) throws Exception {
        MultipleEvaluationsResults results = evaluateClassifierBootstrapping(ensemble, dataPath, runs);
        computeDiversity(runs, diversityStrategy, baseCassifierResults, results);
        return results;
    }

    private static void computeDiversity(int runs, EnsembleDiversityStrategy diversityStrategy, List<MultipleEvaluationsResults> baseCassifierResults, MultipleEvaluationsResults ensembleResults) {
        if (diversityStrategy != null) {
            for (int i = 0; i < runs; i++) {
                final int finalI = i;
                ensembleResults.get(finalI).setDiversity(diversityStrategy.calcDiversity(
                        ensembleResults.get(finalI),
                        baseCassifierResults.stream().map(baseClassifierResult -> baseClassifierResult.get(finalI)).toList()));
            }
        }
    }

}
