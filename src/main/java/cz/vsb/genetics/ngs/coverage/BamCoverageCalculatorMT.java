/*
 * Copyright (C) 2021  Tomas Novosad
 * VSB-TUO, Faculty of Electrical Engineering and Computer Science
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.vsb.genetics.ngs.coverage;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageCalculator;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.ngs.bam.BamUtils;
import htsjdk.samtools.SamReader;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class BamCoverageCalculatorMT implements CoverageCalculator {
    private SamReader[] samReaders;
    private final int threads;
    private final String bamFile;
    private final String indexFile;
    private int mappingQuality = 0;

    public BamCoverageCalculatorMT(String bamFile, String indexFile, int threads) {
        this.threads = threads;
        this.bamFile = bamFile;
        this.indexFile = indexFile;
    }

    @Override
    public void open() {
        samReaders = new SamReader[threads];

        for (int i = 0; i < threads; i++)
            samReaders[i] = BamUtils.getSamReader(bamFile, indexFile);
    }

    @Override
    public void close() throws Exception {
        for (SamReader samReader : samReaders)
            samReader.close();
    }

    @Override
    public int getPositionCoverage(Chromosome chromosome, int position) {
        return BamCoverageUtils.getCoverage(chromosome, position, samReaders[0], mappingQuality);
    }

    @Override
    public CoverageInfo getIntervalCoverage(Chromosome chromosome, int start, int end) throws Exception {
        return calculateCoverage(chromosome, start, end);
    }

    private CoverageInfo calculateCoverage(Chromosome chromosome, int start, int end) throws Exception {
        List<IntervalCoverageCalculator> calculators = new ArrayList<>();

        int delta = (end - start) / threads;

        for (int i = 0; i < threads; i++) {
            int a = start + i * delta;
            int b = i == threads - 1 ? end : a + delta - 1;

            IntervalCoverageCalculator calculator = new IntervalCoverageCalculator(chromosome, a, b, samReaders[i], mappingQuality);
            calculators.add(calculator);
            calculator.start();
        }

        for (IntervalCoverageCalculator calculator : calculators)
            calculator.join();

        int[] coverages = new int[0];
        int maxCoverage = 0;
        int minCoverage = Integer.MAX_VALUE;
        for (IntervalCoverageCalculator calculator : calculators) {
            CoverageInfo coverageInfo = calculator.getCoverageInfo();

            coverages = ArrayUtils.addAll(coverages, coverageInfo.getCoverages());

            if (coverageInfo.getMaxCoverage() > maxCoverage)
                maxCoverage = coverageInfo.getMaxCoverage();

            if (coverageInfo.getMinCoverage() < minCoverage)
                minCoverage = coverageInfo.getMinCoverage();
        }

        return new CoverageInfo(coverages, minCoverage, maxCoverage, start, end);
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) throws Exception {
        double sum = 0.0;
        int[] coverages = getIntervalCoverage(chromosome, start, end).getCoverages();

        for (int coverage : coverages)
            sum += coverage;

        return sum / (double)coverages.length;
    }

    @Override
    public void setMappingQuality(int mappingQuality) {
        this.mappingQuality = mappingQuality;
    }

    private static class IntervalCoverageCalculator extends Thread {
        CoverageInfo coverageInfo;
        private final SamReader samReader;
        private final Chromosome chromosome;
        private final int start;
        private final int end;
        private final int mappingQuality;

        public IntervalCoverageCalculator(Chromosome chromosome, int start, int end, SamReader samReader, int mappingQuality) {
            this.samReader = samReader;
            this.chromosome = chromosome;
            this.start = start;
            this.end = end;
            this.mappingQuality = mappingQuality;
        }

        @Override
        public void run() {
            coverageInfo = BamCoverageUtils.getCoverage(chromosome, start, end, samReader, mappingQuality);
        }

        public CoverageInfo getCoverageInfo() {
            return coverageInfo;
        }
    }
}
