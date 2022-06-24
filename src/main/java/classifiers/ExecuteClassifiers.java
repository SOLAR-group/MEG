package classifiers;

import classifiers.ensemble.stacking.StackingClassifier;
import classifiers.ensemble.stacking.strategy.MCCStackingStrategy;
import classifiers.ensemble.stacking.strategy.StackingStrategy;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.builder.DiversityStrategyFactory;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import multiobjectiveoptimization.objective.diversity.composite.pairwise.AbstractPairwiseEnsembleDiversityStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import preprocessing.Preprocessing;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

public class ExecuteClassifiers implements Callable<Integer> {
    @Option(names = {"--diversity", "-d"},
            description = "The name of the diversity measure to use")
    private String diversity = "DISAGREEMENT";

    @Option(names = {"--project", "-p"},
            description = "The name of the project to use")
    private String projectToUse = "activemq";

    @Option(names = {"--isFirst", "-f"},
            description = "Whether we are testing first-last version pair, otherwise it is penultimate-last pair")
    private boolean isFirst = true;

    private static final String titleFormat = "================== Evaluation results of %s classifiers ================== ";
    private static int NUM_RUNS = 1;

    public ExecuteClassifiers() {
    }

    public static void main(String[] args) {
        String[] validProjects = {"activemq", "hive", "groovy", "lucene", "wicket",
//                "camel",
                "derby", "hbase", "jruby"};
        for (Boolean first : new Boolean[]{true, false}) {
            for (String project : validProjects) {
                ExecuteClassifiers executer = new ExecuteClassifiers();
                executer.projectToUse = project;
                executer.isFirst = first;
                CommandLine commandLine = new CommandLine(executer);
                int exitCode = commandLine.execute(args);
            }
        }
    }

    @Override
    public Integer call() throws Exception {
        List<Class<? extends AbstractExecutableClassifier>> clazzes = new ArrayList<>();

        String[] validProjects = {"activemq", "hive", "groovy", "lucene", "wicket", "camel", "derby", "hbase", "jruby"};
        if (!ArrayUtils.contains(validProjects, projectToUse)) {
            throw new IllegalArgumentException("Invalid project");
        }

        System.out.println("Project is " + projectToUse);
        System.out.println("Diversity used is: " + diversity);

        ClassificationObjective diversityObjective = ClassificationObjective.valueOf(diversity);
//        EnsembleDiversityStrategy diversityStrategy = DiversityStrategyFactory.buildStrategy(diversityObjective);

        if (diversity.equals("DISAGREEMENT")) {
            clazzes.addAll(Arrays.asList(
                    BaseClassifiers.class
//                    BaggingClassifier.class,
//                    BoostingClassifier.class
            ));
        }

//        clazzes.add(StackingClassifier.class);

//        if (!(diversityStrategy instanceof AbstractPairwiseEnsembleDiversityStrategy)) {
//            throw new IllegalArgumentException("You can only use pairwise diversity measures for stacking");
//        }
//
//        AbstractPairwiseEnsembleDiversityStrategy diversityStrategyForStacking = (AbstractPairwiseEnsembleDiversityStrategy) diversityStrategy;
//        System.out.println(diversityStrategyForStacking);

        Preprocessing preprocess = new Preprocessing();

        File dir = new File("data/Arff_XV"); // cross version
        File[] files = dir.listFiles();

        if (files == null) return 0;

        Map<String, List<File>> projects = new HashMap<>();


        for (File file : files) {
            String projectName = file.getName().split("-")[0];
            projects.putIfAbsent(projectName, new ArrayList<>());
            projects.get(projectName).add(file);
        }

        for (String project : projects.keySet()) {
            if (!project.equals(projectToUse)) continue;

            List<File> versions = projects.get(project);

            String outputName = project;

            String firstOrPenultimate = isFirst ? "first" : "penultimate";
            File firstVersion = versions.stream().filter(file -> StringUtils.substringAfterLast(file.getName(), "-").equals(firstOrPenultimate + ".arff")).findFirst().orElse(null);
            File lastVersion = versions.stream().filter(file -> StringUtils.substringAfterLast(file.getName(), "-").equals("last.arff")).findFirst().orElse(null);

            String arffPathTraining = firstVersion.toString();

            if (!arffPathTraining.contains(".arff")) {
                continue;
            }

            System.out.println("====================== " + project + "=====================");
            System.out.println("Training version: " + firstVersion.getName());
            System.out.println("Last version: " + lastVersion.getName());

            Instances trainingData = preprocess.preprocessCrossVersionData(firstVersion.toString());
            Instances testData = preprocess.preprocessCrossVersionData(lastVersion.toString());

//            BaseClfPerformanceResults performanceResults = BaseClassifiers.getPerformances(arffPathTraining, diversityStrategyForStacking);
//            String bestClfByPrecision = performanceResults.getNameOfBestClfFor(ClassificationObjective.PRECISION);
//            String bestClfByMCC = performanceResults.getNameOfBestClfFor(ClassificationObjective.MCC);
//            Pair<AbstractClassifier, AbstractClassifier> bestClfs =
//                    new ImmutablePair<>(BaseClassifiers.buildBClf(bestClfByMCC), BaseClassifiers.buildBClf(bestClfByPrecision));

            System.out.println("Number of instances used for training: " + trainingData.numInstances());
            System.out.println("Number of instances used for testing: " + testData.numInstances());
            System.out.println();

//            List<StackingStrategy> stackingStrategies = new ArrayList<>();
////            stackingStrategies.add(new DIVStackingStrategy(performanceResults.getDIVResults()));
////            stackingStrategies.add(new WADStackingStrategy(performanceResults.getWADResults()));
//
////            if(diversity.equals("DISAGREEMENT")) {
////                stackingStrategies.add(new PrecisionStackingStrategy(performanceResults.getPrecisionResults()));
//            stackingStrategies.add(new MCCStackingStrategy(performanceResults.getMCCResults()));
//            }

            for (Class<? extends AbstractExecutableClassifier> clazz : clazzes) {
                Pair<AbstractClassifier, AbstractClassifier> selectedClfs = null;

                if (clazz.getSimpleName().equals("BaseClassifiers")) {
                    selectedClfs = null;
                }

//                if (clazz.getSimpleName().equals("StackingClassifier")) {
//                    for (StackingStrategy strategy : stackingStrategies) {
//                        AbstractExecutableClassifier stacking = clazz.getDeclaredConstructor(StackingStrategy.class, Instances.class, Instances.class).newInstance(strategy, trainingData, testData);
//                        genStat(stacking, selectedClfs, project, firstVersion, lastVersion, outputName);
//                    }
//                    continue;
//                }

                AbstractExecutableClassifier clf = clazz.getDeclaredConstructor(Instances.class, Instances.class).newInstance(trainingData, testData);
                genStat(clf, selectedClfs, project, firstVersion, lastVersion, outputName);
            }
        }

        return 0;
    }

    public void genStat(AbstractExecutableClassifier clf, Pair<AbstractClassifier, AbstractClassifier> selectedClfs, String projectName,
                        File trainingVersion, File testingVersion, String fileNameToWrite) throws Exception {
        String firstOrPenultimate = isFirst ? "first" : "penultimate";
        Path fullFilePath = Paths.get("src/evalResult/crossVersion/" + diversity, firstOrPenultimate, clf.toString(), fileNameToWrite);
        ExperimentResultWriter writer = new ExperimentResultWriter(fullFilePath);

        writer.writeNewline(projectName);
        writer.writeNewline("Training version: " + trainingVersion.getName());
        writer.writeNewline("Testing version: " + testingVersion.getName());

        writer.writeNewline(String.format(titleFormat, clf));
        writer.newline();

        clf.runExperiment(selectedClfs, writer, NUM_RUNS);
        writer.close();
    }
}
