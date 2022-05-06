package cz.vsb.genetics.metrics.ngs;

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
