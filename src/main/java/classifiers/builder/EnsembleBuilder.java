package classifiers.builder;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to build ensemble classifiers.
 */
public class EnsembleBuilder {

    /**
     * Generates a set of (untrained) ensemble classifiers with the given solution list.
     *
     * @param solutions the list of solutions
     *
     * @return the ensemble classifiers
     *
     * @throws Exception in case Weka cannot generate the classifier
     */
    public List<MultipleClassifiersCombiner> buildEnsembles(List<CompositeSolution> solutions) throws Exception {
        List<MultipleClassifiersCombiner> result = new ArrayList<>(solutions.size());
        for (CompositeSolution solution : solutions) {
            result.add(buildEnsemble(solution));
        }
        return result;
    }

    private MultipleClassifiersCombiner buildEnsemble(CompositeSolution solution) throws Exception {
        ClassifiersBuilder builder = new ClassifiersBuilder();
        List<AbstractClassifier> classifiers = builder.buildClassifiers((BinarySolution) solution.getVariable(0), (DoubleSolution) solution.getVariable(1));
        MultipleClassifiersCombiner ensemble = this.buildEnsemble(((IntegerSolution) solution.getVariable(2)).getVariable(0), classifiers);
        return ensemble;
    }

    /**
     * Generates an (untrained) ensemble classifiers with the given classifiers.
     *
     * @param method      the type of ensemble strategy. 1) majority voting; 2) weighted majority voting; 3) stacking;
     *                    and 4) average rule.
     * @param classifiers the classifiers to be used in the ensemble.
     *
     * @return the ensemble classifier
     *
     * @throws Exception in case Weka cannot generate the classifier
     */
    public MultipleClassifiersCombiner buildEnsemble(int method, List<AbstractClassifier> classifiers) throws Exception {
        return switch (method) {
            case 0 -> buildMajorityVoting(classifiers);
            case 1 -> buildWeightedMajorityVoting(classifiers);
            case 2 -> buildStacking(classifiers);
            case 3 -> buildAverageRuleVoting(classifiers);
            default -> null;
        };
    }


    /**
     * Builds an average rule ensemble.
     *
     * @param classifiers the classifiers of the ensemble
     *
     * @return the ensemble classifier
     *
     * @throws Exception in case Weka cannot generate the classifier
     */
    public MultipleClassifiersCombiner buildAverageRuleVoting(List<AbstractClassifier> classifiers) throws Exception {
        String[] opts = new String[]{"-R", "AVG"};
        Vote voter = new Vote();
        voter.setOptions(opts);
        voter.setClassifiers(classifiers.toArray(new AbstractClassifier[0]));
        return voter;
    }

    /**
     * Builds a stacking ensemble. The first classifier of the list is used as the meta classifier.
     *
     * @param classifiers the classifiers of the ensemble
     *
     * @return the ensemble classifier
     */
    public MultipleClassifiersCombiner buildStacking(List<AbstractClassifier> classifiers) {
        Stacking stacker = new Stacking();
        try {
            stacker.setMetaClassifier(AbstractClassifier.makeCopy(classifiers.get(0)));
            stacker.setClassifiers(classifiers.toArray(new AbstractClassifier[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stacker;
    }

    /**
     * Builds a weighted average rule ensemble. The first classifier of the list is added twice to the list of
     * classifiers, so its voting is counted with more weight.
     *
     * @param classifiers the classifiers of the ensemble
     *
     * @return the ensemble classifier
     *
     * @throws Exception in case Weka cannot generate the classifier
     */
    public MultipleClassifiersCombiner buildWeightedMajorityVoting(List<AbstractClassifier> classifiers) throws Exception {
        String[] opts = new String[]{"-R", "MAJ"};
        classifiers = new ArrayList<>(classifiers);
        classifiers.add((AbstractClassifier) AbstractClassifier.makeCopy(classifiers.get(0)));
        Vote voter = new Vote();
        voter.setOptions(opts);
        voter.setClassifiers(classifiers.toArray(new AbstractClassifier[0]));
        return voter;
    }

    /**
     * Builds a majority voting ensemble.
     *
     * @param classifiers the classifiers of the ensemble
     *
     * @return the ensemble classifier
     *
     * @throws Exception in case Weka cannot generate the classifier
     */
    public MultipleClassifiersCombiner buildMajorityVoting(List<AbstractClassifier> classifiers) throws Exception {
        String[] opts = new String[]{"-R", "MAJ"};
        Vote voter = new Vote();
        voter.setOptions(opts);
        voter.setClassifiers(classifiers.toArray(new AbstractClassifier[0]));
        return voter;
    }

}
