package cz.vsb.genetics.coverage;

import java.util.Arrays;

public class CoverageStatistics {
    int min;
    int q1;
    int median;
    int q3;
    int max;
    int mean;
    int standardDeviation;

    public void calculateStatistics(CoverageInfo coverageInfo) {
        Integer[] coverages = Arrays.stream(coverageInfo.getCoverages()).boxed().toArray(Integer[]::new);
        Arrays.sort(coverages);

        min = coverages[0];
        q1 = percentile(coverages, 25.0);
        median = percentile(coverages, 50.0);
        q3 = percentile(coverages, 75.0);
        max = coverages[coverages.length - 1];
        mean = (int)mean(coverages);
        standardDeviation = (int)standardDeviation(coverages);
    }

    private int percentile(Integer[] values, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * values.length);
        return values[index-1];
    }

    private double mean(Integer[] values) {
        long sum = 0;
        for (int value : values)
            sum += value;


        return (double)sum / (double)values.length;
    }

    private double standardDeviation(Integer[] values) {
        double mean = mean(values);

        double variance = 0.0;
        for (Integer value : values)
            variance += Math.pow(mean - (double) value, 2.0);

        variance /= values.length;

        return Math.sqrt(variance);
    }

    public int min() {
        return min;
    }

    public int q1() {
        return q1;
    }

    public int median() {
        return median;
    }

    public int q3() {
        return q3;
    }

    public int max() {
        return max;
    }

    public int mean() {
        return mean;
    }

    public int standardDeviation() {
        return standardDeviation;
    }
}
