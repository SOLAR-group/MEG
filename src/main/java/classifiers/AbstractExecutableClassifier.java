package classifiers;

import org.apache.commons.lang3.tuple.Pair;
import preprocessing.Preprocessing;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

public abstract class AbstractExecutableClassifier {
    protected Instances trainingData;
    protected Instances testData;
    public static final String[] clfNames = new String[]{
            "NaiveBayes-K","NaiveBayes",
            "IBk-K3", "IBk-K5", "IBk-K7",
            "SMO-C1.0", "SMO-C10.0", "SMO-C25.0", "SMO-C50.0",
            "J48-C0.25", "J48-C0.2", "J48-C0.15", "J48-C0.1", "J48-C0.05"
    };
    protected final Preprocessing preprocess = new Preprocessing();
    private final String type;

    public AbstractExecutableClassifier(String type, Instances trainingData, Instances testData) {
        this.type = type;
        this.trainingData = trainingData;
        this.testData = testData;
    }

    public static String getNameWithParameterFromClassifier(AbstractClassifier classifier) {
        String name = classifier.getClass().getSimpleName();
        StringBuilder sb = new StringBuilder(name);

        if (classifier.getOptions().length != 0) {
            // For default NaiveBayes, getOptions() returns []
            sb.append(classifier.getOptions()[0]);
        }

        if (!name.equals("NaiveBayes")) {
            // for other classifiers, we have the argument value after the flag
            // e.g. for IBk-K3, first two strings in getOptions are: ["-K", "3"]
            sb.append(classifier.getOptions()[1]);
        }

        return sb.toString();
    }

    public abstract void runExperiment(Pair<AbstractClassifier, AbstractClassifier> bestClfs, ExperimentResultWriter writer, int runs) throws Exception;

    @Override
    public String toString() {
        return this.type;
    }
}
