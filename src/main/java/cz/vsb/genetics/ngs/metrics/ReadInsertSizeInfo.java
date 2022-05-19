package cz.vsb.genetics.ngs.metrics;

public class ReadInsertSizeInfo {
    int minInsertSize = Integer.MAX_VALUE;
    int maxInsertSize = Integer.MIN_VALUE;
    double meanInsertSize = 0.0;
    double insertSizeStd = 0.0;

    public int getMinInsertSize() {
        return minInsertSize;
    }

    public int getMaxInsertSize() {
        return maxInsertSize;
    }

    public double getMeanInsertSize() {
        return meanInsertSize;
    }

    public double getInsertSizeStd() {
        return insertSizeStd;
    }
}
