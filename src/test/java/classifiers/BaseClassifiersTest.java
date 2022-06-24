package classifiers;

import org.junit.jupiter.api.Test;
import weka.classifiers.AbstractClassifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseClassifiersTest {
    @Test
    void BuildingClassifierWorks() throws Exception {
        for (String clfName : BaseClassifiers.clfNames) {
            AbstractClassifier classifier = BaseClassifiers.buildBClf(clfName);
            String name = BaseClassifiers.getNameWithParameterFromClassifier(classifier);
            assertEquals(clfName, name);
        }
    }
}
