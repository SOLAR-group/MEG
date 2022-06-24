package classifiers.ensemble;

import classifiers.AbstractExecutableClassifier;
import classifiers.ExperimentResultWriter;
import org.apache.commons.lang3.tuple.Pair;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class BoostingClassifier extends AbstractExecutableClassifier {
    public BoostingClassifier(Instances trainingData, Instances testData) {
        super("Boosting", trainingData, testData);
        System.out.println("BoostingClassifier class called");
    }

    private void evalBoosting(AbstractClassifier metaClf, ExperimentResultWriter writer, int runs) throws Exception {
        String name = this + " with meta: " + getNameWithParameterFromClassifier(metaClf);

        writer.writeNewline("==========================================================");
        writer.writeNewline(name);
        writer.newline();

        List<Evaluation> runResults = new ArrayList<>();

        for (int i = 0; i < runs; i++) {
            AdaBoostM1 boost = new AdaBoostM1();
            boost.setClassifier(metaClf);
            boost.setNumIterations(20);

            boost.buildClassifier(trainingData);
            Evaluation wekaEvaluation = new Evaluation(trainingData);
            wekaEvaluation.evaluateModel(boost, testData);
            runResults.add(wekaEvaluation);
        }

        writer.writeRunResults(runResults);
    }

    @Override
    public void runExperiment(Pair<AbstractClassifier, AbstractClassifier> bestClfs, ExperimentResultWriter writer, int runs) throws Exception {
        AbstractClassifier clfBestMCC = bestClfs.getLeft();
        AbstractClassifier clfBestDef = bestClfs.getRight();

        System.out.println(".............. Boosting Classifier ................");
        System.out.println("Using the following to build boosting classifiers: ");
        System.out.println(getNameWithParameterFromClassifier(clfBestMCC) + " (Best MCC) ");
        System.out.println(getNameWithParameterFromClassifier(clfBestDef) + " (Best Defective Precision)");

        evalBoosting(clfBestMCC, writer, runs);
        if (clfBestMCC.equals(clfBestDef)) {
            return;
        }
        evalBoosting(clfBestDef, writer, runs);
    }
}
