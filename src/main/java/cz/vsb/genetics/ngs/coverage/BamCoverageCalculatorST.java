/*
 * Copyright (C) 2025  Tomas Novosad
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

public class BamCoverageCalculatorST implements CoverageCalculator {
    private SamReader samReader;
    private final String bamFile;
    private final String indexFile;
    private int mappingQuality = 0;

    public BamCoverageCalculatorST(String bamFile, String indexFile) {
        this.bamFile = bamFile;
        this.indexFile = indexFile;
    }

    @Override
    public void open() {
        samReader = BamUtils.getSamReader(bamFile, indexFile);
    }

    @Override
    public void close() throws Exception {
        samReader.close();
    }

    @Override
    public int getPositionCoverage(Chromosome chromosome, int position) {
        return BamCoverageUtils.getCoverage(chromosome, position, samReader, mappingQuality);
    }

    @Override
    public CoverageInfo getIntervalCoverage(Chromosome chromosome, int start, int end) {
        return BamCoverageUtils.getCoverage(chromosome, start, end, samReader, mappingQuality);
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) {
        return BamCoverageUtils.getMeanCoverage(chromosome, start, end, samReader, mappingQuality);
    }

    @Override
    public void setMappingQuality(int mappingQuality) {
        this.mappingQuality = mappingQuality;
    }
}
