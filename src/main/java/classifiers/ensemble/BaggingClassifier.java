package classifiers.ensemble;

import classifiers.AbstractExecutableClassifier;
import classifiers.ExperimentResultWriter;
import org.apache.commons.lang3.tuple.Pair;
import util.Utils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaggingClassifier extends AbstractExecutableClassifier {
    public BaggingClassifier(Instances trainingData, Instances testData) {
        super("Bagging", trainingData, testData);
        System.out.println("BaggingClassifier class called");
    }

    private void evalBagging(AbstractClassifier metaClf, ExperimentResultWriter writer, int runs) throws Exception {
        String name = this + " with meta: " + getNameWithParameterFromClassifier(metaClf);

        writer.writeNewline("==========================================================");
        writer.writeNewline(name);
        writer.newline();

        List<Evaluation> runResults = new ArrayList<>();

        for (int i = 0; i < runs; i++) {
            Bagging bagger = new Bagging();
            bagger.setClassifier(metaClf);
            bagger.setNumIterations(20);

            bagger.buildClassifier(trainingData);
            Evaluation wekaEvaluation = new Evaluation(trainingData);
            wekaEvaluation.evaluateModel(bagger, testData);
            runResults.add(wekaEvaluation);
        }

        writer.writeRunResults(runResults);
    }

    @Override
    public void runExperiment(Pair<AbstractClassifier, AbstractClassifier> bestClfs, ExperimentResultWriter writer, int runs) throws Exception {
        AbstractClassifier clfBestMCC = bestClfs.getLeft();
        AbstractClassifier clfBestDef = bestClfs.getRight();

        System.out.println(".............. Bagging Classifier ................");
        System.out.println("Using the following to build bagging classifiers: ");
        System.out.println(getNameWithParameterFromClassifier(clfBestMCC) + " (Best MCC) ");
        System.out.println(getNameWithParameterFromClassifier(clfBestDef) + " (Best Defective Precision)");

        evalBagging(clfBestMCC, writer, runs);
        if (clfBestMCC.equals(clfBestDef)) {
            return;
        }
        evalBagging(clfBestDef, writer, runs);
    }
}
