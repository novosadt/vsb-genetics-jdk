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
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.ngs.bam.BamUtils;
import htsjdk.samtools.SamReader;

public class BamCoverageInfoST implements CoverageInfo {
    private SamReader samReader;
    private final String bamFile;
    private final String indexFile;

    public BamCoverageInfoST(String bamFile, String indexFile) {
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
    public long getPositionCoverage(Chromosome chromosome, int position) {
        return BamCoverageInfoUtils.getCoverage(chromosome, position, samReader);
    }

    @Override
    public long[] getIntervalCoverage(Chromosome chromosome, int start, int end) {
        return BamCoverageInfoUtils.getCoverage(chromosome, start, end, samReader);
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) {
        return BamCoverageInfoUtils.getMeanCoverage(chromosome, start, end, samReader);
    }
}
