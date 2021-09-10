package cz.vsb.genetics.ngs.coverage;

import cz.vsb.genetics.common.Chromosome;
import htsjdk.samtools.SamReader;

import java.util.ArrayList;
import java.util.List;

public class BamCoverageInfoMT extends BamCoverageInfoUtils implements BamCoverageInfo {
    private final SamReader[] samReaders;
    private final int threads;

    public BamCoverageInfoMT(String bamFile, String indexFile, int threads) {
        this.threads = threads;
        samReaders = new SamReader[threads];

        for (int i = 0; i < threads; i++)
            samReaders[i] = getSamReader(bamFile, indexFile);
    }

    @Override
    public void close() throws Exception {
        for (SamReader samReader : samReaders)
            samReader.close();
    }

    @Override
    public long getCoverage(Chromosome chromosome, int position) {
        return BamCoverageInfoUtils.getCoverage(chromosome, position, samReaders[0]);
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end) throws Exception {
        return calculateCoverage(chromosome, start, end, -1);
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end, int step) throws Exception {
        return calculateCoverage(chromosome, start, end, step);
    }

    public List<Long> calculateCoverage(Chromosome chromosome, int start, int end, int step) throws Exception {
        List<IntervalCoverageCalculator> calculators = new ArrayList<>();

        int delta = (end - start) / threads;

        for (int i = 0; i < threads; i++) {
            int a = start + i * delta;
            int b = i == threads - 1 ? end : a + delta - 1;

            IntervalCoverageCalculator calculator = step == -1
                    ? new IntervalCoverageCalculator(chromosome, a, b, samReaders[i])
                    : new IntervalCoverageCalculator(chromosome, a, b, step, samReaders[i]);

            calculators.add(calculator);
            calculator.start();
        }

        for (IntervalCoverageCalculator calculator : calculators)
            calculator.join();

        List<Long> coverages = new ArrayList<>();
        for (IntervalCoverageCalculator calculator : calculators)
            coverages.addAll(calculator.getCoverages());

        return coverages;
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) throws Exception {
        double sum = 0.0;
        List<Long> coverages = getCoverage(chromosome, start, end);

        for (long coverage : coverages)
            sum += (double)coverage;

        return sum / (double)coverages.size();
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end, int step) throws Exception {
        double sum = 0.0;
        List<Long> coverages = getCoverage(chromosome, start, end, step);

        for (long coverage : coverages)
            sum += (double)coverage;

        return sum / (double)coverages.size();
    }

    private static class IntervalCoverageCalculator extends Thread {
        private List<Long> coverages;
        private final SamReader samReader;
        private final Chromosome chromosome;
        private final int start;
        private final int end;
        private int step = -1;

        public IntervalCoverageCalculator(Chromosome chromosome, int start, int end, SamReader samReader) {
            this.samReader = samReader;
            this.chromosome = chromosome;
            this.start = start;
            this.end = end;
        }

        public IntervalCoverageCalculator(Chromosome chromosome, int start, int end, int step, SamReader samReader) {
            this(chromosome, start, end, samReader);
            this.step = step;
        }

        @Override
        public void run() {
            if (step == -1)
                coverages = BamCoverageInfoUtils.getCoverage(chromosome, start, end, samReader);
            else
                coverages = BamCoverageInfoUtils.getCoverage(chromosome, start, end, step, samReader);
        }

        public List<Long> getCoverages() {
            return coverages;
        }
    }
}
