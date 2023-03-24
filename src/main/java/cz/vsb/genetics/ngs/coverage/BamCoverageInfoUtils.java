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
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;

public class BamCoverageInfoUtils {


    public static long getCoverage(Chromosome chromosome, int position, SamReader samReader) {
        SAMRecordIterator it = samReader.queryOverlapping(chromosome.toString(), position, position);

        long count = it.hasNext() ? it.stream().count() : 0;

        it.close();

        return count;
    }

    public static long[] getCoverage(Chromosome chromosome, int start, int end, SamReader samReader) {
        if (end < start)
            return new long[0];

        if (end - start == 0)
            return new long[]{getCoverage(chromosome, start, samReader)};

        long[] coverages = new long[end - start + 1];

        try (SAMRecordIterator it = samReader.queryOverlapping(chromosome.toString(), start, end)) {
            while(it.hasNext()) {
                SAMRecord samRecord = it.next();

                for (AlignmentBlock alignmentBlock : samRecord.getAlignmentBlocks()) {
                    int startPosition = alignmentBlock.getReferenceStart();
                    int endPosition = startPosition + alignmentBlock.getLength();
                    int from = 0;
                    int to = 0;

                    // alignment is inside the region of interest
                    if (startPosition >= start && endPosition <= end) {
                        from = startPosition - start;
                        to = endPosition - startPosition + 1;
                    }
                    // alignment overlaps whole region of interest
                    else if (startPosition <= start && endPosition >= end) {
                        to = coverages.length;
                    }
                    // alignment starts outside the region and ends inside
                    else if (startPosition <= start) {
                        to = endPosition - start + 1;
                    }
                    // alignment starts inside the region and ends outside
                    else if (endPosition >= end) {
                        from = startPosition - start;
                        to = coverages.length;
                    }

                    for (int i = from; i < to; i++)
                        coverages[i]++;
                }
            }
        }

        return coverages;
    }

    public static double getMeanCoverage(Chromosome chromosome, int start, int end, SamReader samReader) {
        double sum = 0.0;
        long[] coverages = getCoverage(chromosome, start, end, samReader);

        for (long coverage : coverages)
            sum += coverage;

        return sum / (double)coverages.length;
    }
}
