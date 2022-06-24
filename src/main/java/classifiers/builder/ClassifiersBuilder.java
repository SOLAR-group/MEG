package classifiers.builder;

import classifiers.BaseClassifiers;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for building classifiers given a jMetal solution.
 */
public class ClassifiersBuilder {

    /**
     * Given a set of bits, parameters, and the index of the classifier, build them.
     *
     * @param binarySolution the bit set of classifiers
     * @param parameters     the set of parameters to be used during building
     *
     * @return a list of classifiers (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public List<AbstractClassifier> buildClassifiers(BinarySolution binarySolution, DoubleSolution parameters) throws Exception {
        List<AbstractClassifier> result = new ArrayList<>();
        for (int i = 0; i < binarySolution.getNumberOfBits(0); i++) {
            if (binarySolution.getVariable(0).get(i)) {
                result.add(this.buildClassifiers(parameters, i));
            }
        }
        return result;
    }

    /**
     * Given a set of parameters and the index of the classifier, build it.
     *
     * @param parameters the set of parameters to be used during building
     * @param i          the index of the classifier in the bitset. This is also used to query the parameter.
     *
     * @return a classifier (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public AbstractClassifier buildClassifiers(DoubleSolution parameters, int i) throws Exception {
        return switch (i) {
            case 0, 1, 2 -> buildNaiveBayes(parameters, i);
            case 3, 4, 5 -> buildKNN(parameters, i);
            case 6, 7, 8, 9 -> buildSVM(parameters, i);
            case 10, 11, 12, 13, 14 -> buildTree(parameters, i);
            default -> null;
        };
    }

    /**
     * Builds a Naive Bayes classifier.
     *
     * @param parameters the set of parameters to be used during building
     * @param i          the index of the parameter.
     *
     * @return a classifier (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public NaiveBayes buildNaiveBayes(DoubleSolution parameters, int i) throws Exception {
        int nbParameter = parameters.getVariable(i).intValue();
        String[] nbOpt = new String[1];
        if (nbParameter == 1) {
            nbOpt[0] = "-K";
        } else if (nbParameter == 0) {
            nbOpt = new String[0];
        }
        return BaseClassifiers.buildNB(nbOpt);
    }

    /**
     * Builds a KNN classifier.
     *
     * @param parameters the set of parameters to be used during building
     * @param i          the index of the parameter.
     *
     * @return a classifier (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public IBk buildKNN(DoubleSolution parameters, int i) throws Exception {
        double knnParameter = parameters.getVariable(i);
        int knnParameter_ = (int) knnParameter;

        String[] opt_knn = new String[]{"-K", String.valueOf(knnParameter_)};
        return BaseClassifiers.buildKNN(opt_knn);
    }

    /**
     * Builds an SVM classifier.
     *
     * @param parameters the set of parameters to be used during building
     * @param i          the index of the parameter.
     *
     * @return a classifier (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public SMO buildSVM(DoubleSolution parameters, int i) throws Exception {
        double svmPara = parameters.getVariable(i);
        int svmPara1 = (int) svmPara;

        String[] opt_svm = new String[]{"-C", String.valueOf(svmPara1)};
        return BaseClassifiers.buildSVM(opt_svm);
    }

    /**
     * Builds a Decision Tree classifier.
     *
     * @param parameters the set of parameters to be used during building
     * @param i          the index of the parameter.
     *
     * @return a classifier (untrained)
     *
     * @throws Exception in case Weka cannot build it
     */
    public J48 buildTree(DoubleSolution parameters, int i) throws Exception {
        double treePara = parameters.getVariable(i);
        String[] opt_tree = new String[]{"-C", String.valueOf(treePara)};
        return BaseClassifiers.buildTree(opt_tree);
    }

}
