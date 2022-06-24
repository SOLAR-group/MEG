package classifiers;

import classifiers.ensemble.stacking.StackingClassifier;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import preprocessing.Preprocessing;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Stacking;
import weka.core.Instances;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BaseClfPerformanceResultsTest {
    @Test
    void EvaluatingStackerDoesNotThrowError() {
        assertDoesNotThrow(
                () -> {
                    File dir = new File("data/Arff_XV"); // cross version
                    File[] files = dir.listFiles();
                    Map<String, List<File>> projects = new HashMap<>();

                    for (File file : files) {
                        String projectName = file.getName().split("-")[0];
                        projects.putIfAbsent(projectName, new ArrayList<>());
                        projects.get(projectName).add(file);
                    }

                    for (String project : projects.keySet()) {
                        System.out.println("Project: " + project);
                        List<File> versions = projects.get(project);
                        String[] clfNames = AbstractExecutableClassifier.clfNames;
                        clfNames = Arrays.copyOfRange(clfNames, 0, 3);
                        File firstVersion = versions.stream().filter(file -> StringUtils.substringAfterLast(file.getName(), "-").equals("first.arff")).findFirst().orElse(null);
                        String arffPath = firstVersion.toString();
                        System.out.println("ArffPath:"+ arffPath);

                        for (String meta : clfNames) {
                            System.out.println("Meta: " + meta);
                            Classifier metaClf = BaseClassifiers.buildBClf(meta);
                            Classifier[] stackedBClfs = StackingClassifier.getStackedBClfs(clfNames);
                            Stacking stacker = StackingClassifier.getStacker(metaClf, stackedBClfs);

                            System.out.println("Starting Evaluation.");
                            MultipleEvaluationsResults evaluationResults = ModelEvaluation.evaluateClassifierBootstrapping(stacker, arffPath, 1);
                            System.out.println("Evaluation Done.");

                            double precision = evaluationResults.getAveragePrecision();
                            System.out.println(precision);
                            break;
                        }
                    }
                }
        );
    }
}
