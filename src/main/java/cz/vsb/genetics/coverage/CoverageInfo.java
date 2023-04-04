package cz.vsb.genetics.coverage;

public class CoverageInfo {
    int[] coverages;
    int minCoverage;
    int maxCoverage;
    int start;
    int end;

    public CoverageInfo(int[] coverages, int minCoverage, int maxCoverage) {
        this.coverages = coverages;
        this.minCoverage = minCoverage;
        this.maxCoverage = maxCoverage;
    }

    public int[] getCoverages() {
        return coverages;
    }

    public void setCoverages(int[] coverages) {
        this.coverages = coverages;
    }

    public int getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(int maxCoverage) {
        this.maxCoverage = maxCoverage;
    }

    public int getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(int minCoverage) {
        this.minCoverage = minCoverage;
    }
}
