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


package cz.vsb.genetics.ngs.metrics;

import cz.vsb.genetics.ngs.bam.BamUtils;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;

import java.util.ArrayList;
import java.util.List;

public class ReadSizeMetrics {
    public static ReadInsertSizeInfo estimateInsertSizeMetrics(String bamFile, String indexFile, int estimateReadCount) {
        SamReader reader = BamUtils.getSamReader(bamFile, indexFile);

        List<Integer> insertSizes = new ArrayList<>();
        ReadInsertSizeInfo info = new ReadInsertSizeInfo();
        long sum = 0;
        while(--estimateReadCount > 0 && reader.iterator().hasNext()) {
            SAMRecord record = reader.iterator().next();

            if (record.getReadPairedFlag() && record.getProperPairFlag() && record.getFirstOfPairFlag()) {
                int insertSize = record.getInferredInsertSize();

                if (insertSize > info.maxInsertSize)
                    info.maxInsertSize = insertSize;

                if (insertSize < info.minInsertSize)
                    info.minInsertSize = insertSize;

                sum += insertSize;
                insertSizes.add(insertSize);
            }
        }

        info.meanInsertSize = (double)sum / (double)insertSizes.size();

        sum = 0;
        for (int insertSize : insertSizes)
            sum += Math.sqrt(insertSize - info.meanInsertSize);

        info.insertSizeStd = (double)sum / (double)insertSizes.size();

        return info;
    }

    public static int estimateReadLength(String bamFile, String indexFile, int estimateReadCount) throws Exception {
        SamReader reader = BamUtils.getSamReader(bamFile, indexFile);

        List<Integer> readSizes = new ArrayList<>();

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < estimateReadCount; i++) {
            if (reader.iterator().hasNext())
                break;

            SAMRecord record = reader.iterator().next();

            if (record.getReadLength() > max)
                max = record.getReadLength();

            if (record.getReadLength() < min)
                min = record.getReadLength();
        }

        if (max != min)
            throw new Exception("Reads are of different lengths.");

        return min;
    }
}
