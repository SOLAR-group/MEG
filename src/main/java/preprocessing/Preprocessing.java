package preprocessing;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

import java.util.Arrays;
import java.util.Random;

public class Preprocessing {

    // Remove unnecessary attributes.
    private Instances removeAttris(Instances dataset) throws Exception {
        String[] opts = new String[]{"-R", "1-4"};
        Remove remove = new Remove();
        remove.setOptions(opts);
        remove.setInputFormat(dataset);
        return Filter.useFilter(dataset, remove);
    }

    // Discretize the numerical class attribute to nominal. Map 0, 1 to (-inf-0.5] and [1+inf)
    private Instances discretizeClassAttri(Instances dataset) throws Exception {
        String[] ops = new String[]{"-B", "2", "-R", "21"};
        Discretize discretize = new Discretize();
        discretize.setOptions(ops);
        discretize.setInputFormat(dataset);
        return Filter.useFilter(dataset, discretize);
    }

    // Stratified train-test split. 20% of instances are used for testing
    private Pair<Instances, Instances> trainTestSplit(Instances dataset) {
        dataset.stratify(5);
        Instances train = dataset.trainCV(5, 0);
        Instances test = dataset.testCV(5, 0);

        return new ImmutablePair<>(train, test);
    }

    public Instances getDataSetFromPath(String dataPath) throws Exception {
        DataSource source = new DataSource(dataPath);
        Instances dataset = source.getDataSet();
        dataset = removeAttris(dataset);
        dataset = discretizeClassAttri(dataset);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        return dataset;
    }

    public Pair<Instances, Instances> getTrainTestData(String dataPath) throws Exception {
        Instances dataset = getDataSetFromPath(dataPath);
        return trainTestSplit(dataset);
    }

    public Pair<Instances, Instances> getTrainTestData(String dataPath, boolean randomize, long seed) throws Exception {
        Instances dataset = getDataSetFromPath(dataPath);

        if (randomize) {
            Random rand = new Random(seed);
            dataset.randomize(rand);
        }

        return trainTestSplit(dataset);
    }

    public Instances preprocessCrossVersionData(String dataPath) throws Exception {
        DataSource source = new DataSource(dataPath);
        Instances dataset = source.getDataSet();

        // remove unnecesssary attributes
        Remove remove = new Remove();
        // 0 - File, 66 - HeuBug, 67 - HeuBugCount, 69 - RealBugCount
        remove.setAttributeIndicesArray(new int[]{0, 66, 67, 68});
        remove.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, remove);

        Discretize discretize = new Discretize();
        // Discretize the last column ('RealBug')
        String[] ops = new String[]{"-B", "2", "-R", "last"};
        discretize.setOptions(ops);
        discretize.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, discretize);

        // set the index of the result class
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }
}
