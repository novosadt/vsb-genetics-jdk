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

package cz.vsb.genetics.coverage.ngs;

import cz.vsb.genetics.common.Chromosome;
import htsjdk.samtools.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BamCoverageInfoUtils {
    public static SamReader getSamReader(String bamFile, String indexFile) {
        final SamReaderFactory factory =
                SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);

        final SamInputResource resource = SamInputResource.of(new File(bamFile)).index(new File(indexFile));

        return factory.open(resource);
    }

    public static long getCoverage(Chromosome chromosome, int position, SamReader samReader) {
        SAMRecordIterator it = samReader.queryOverlapping(chromosome.toString(), position, position);

        long count = it.hasNext() ? it.stream().count() : 0;

        it.close();

        return count;
    }

    public static List<Long> getCoverage(Chromosome chromosome, int start, int end, SamReader samReader) {
        List<Long> coverages = new ArrayList<>();

        for (int pos = start; pos <= end; pos++)
            coverages.add(getCoverage(chromosome, pos, samReader));

        return coverages;
    }

    public static List<Long> getCoverage(Chromosome chromosome, int start, int end, int step, SamReader samReader) {
        List<Long> coverages = new ArrayList<>();

        for (int pos = start; pos <= end; pos += step)
            coverages.add(getCoverage(chromosome, pos, samReader));

        //start and end positions should be included
        coverages.add(getCoverage(chromosome, end, samReader));

        return coverages;
    }

    public static double getMeanCoverage(Chromosome chromosome, int start, int end, SamReader samReader) {
        double sum = 0.0;
        List<Long> coverages = getCoverage(chromosome, start, end, samReader);

        for (long coverage : coverages)
            sum += coverage;

        return sum / (double)coverages.size();
    }

    public static double getMeanCoverage(Chromosome chromosome, int start, int end, int step, SamReader samReader) {
        double sum = 0.0;
        List<Long> coverages = getCoverage(chromosome, start, end, step, samReader);

        for (long coverage : coverages)
            sum += coverage;

        return sum / (double)coverages.size();
    }
}
