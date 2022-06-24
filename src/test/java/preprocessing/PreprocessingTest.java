package preprocessing;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PreprocessingTest {
    @Test
    void SplitIsDifferentEachTime() throws Exception {
        // we do it old school here, no messing around with streams :)
        Preprocessing preprocess = new Preprocessing();
        List<List<String>> testInstancesAcrossSplits = new ArrayList<>();
        List<List<String>> trainInstancesAcrossSplits = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            Pair<Instances, Instances> trainTest = preprocess.getTrainTestData("data/Arff/tomcat-clean-sorted.arff", true, i + 1);
            Instances trainingData = trainTest.getLeft();
            Instances testData = trainTest.getRight();
            List<String> testDataStr = new ArrayList<>();
            List<String> trainingDataStr = new ArrayList<>();

            for(Instance inst : testData) {
                testDataStr.add(inst.toString());
            }

            for(Instance inst : trainingData) {
                trainingDataStr.add(inst.toString());
            }

            testInstancesAcrossSplits.add(testDataStr);
            trainInstancesAcrossSplits.add(trainingDataStr);
        }

        boolean failed = false;

        for(int i = 0; i < testInstancesAcrossSplits.size()-1; i++) {
            for(int j = i + 1; j < testInstancesAcrossSplits.size(); j++) {
                List<String> a = testInstancesAcrossSplits.get(i);
                List<String> b = testInstancesAcrossSplits.get(j);
                if(a.size() == b.size() && a.containsAll(b)) {
                    failed = true;
                    break;
                }
            }
            if(failed) break;
        }

        if(!failed) {
            for(int i = 0; i < trainInstancesAcrossSplits.size()-1; i++) {
                for(int j = i + 1; j < trainInstancesAcrossSplits.size(); j++) {
                    List<String> a = trainInstancesAcrossSplits.get(i);
                    List<String> b = trainInstancesAcrossSplits.get(j);
                    if(a.size() == b.size() && a.containsAll(b)) {
                        failed = true;
                        break;
                    }
                }
                if(failed) break;
            }
        }

        assertFalse(failed);
    }
}
