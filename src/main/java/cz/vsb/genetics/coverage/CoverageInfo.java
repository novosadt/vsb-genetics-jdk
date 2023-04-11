package cz.vsb.genetics.coverage;

public class CoverageInfo {
    String title;
    int[] coverages;
    int minCoverage;
    int maxCoverage;
    int positionStart;
    int positionEnd;
    int samplingSize = 0;
    int coverageLimit = 0;

    public CoverageInfo(int[] coverages, int minCoverage, int maxCoverage, int positionStart, int positionEnd) {
        this.coverages = coverages;
        this.minCoverage = minCoverage;
        this.maxCoverage = maxCoverage;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
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

    public int getPositionStart() {
        return positionStart;
    }

    public void setPositionStart(int positionStart) {
        this.positionStart = positionStart;
    }

    public int getPositionEnd() {
        return positionEnd;
    }

    public void setPositionEnd(int positionEnd) {
        this.positionEnd = positionEnd;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSamplingSize() {
        return samplingSize;
    }

    public void setSamplingSize(int samplingSize) {
        this.samplingSize = samplingSize;
    }

    public int getCoverageLimit() {
        return coverageLimit;
    }

    public void setCoverageLimit(int coverageLimit) {
        this.coverageLimit = coverageLimit;
    }
}
