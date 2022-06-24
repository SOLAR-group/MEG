package multiobjectiveoptimization.objective;

import picocli.CommandLine;

public enum ClassificationObjective {
    MCC(false),
    PRECISION(false),
    DISAGREEMENT(true),
    DOUBLE_FAULT(true),
    Q_STATISTIC(true),
    CORRELATION(true),
    HAMMING(true),
    WAD(true);

    private final boolean isDiversity;

    ClassificationObjective(boolean isDiversity) {
        this.isDiversity = isDiversity;
    }

    public boolean isDiversity() {
        return isDiversity;
    }

}
