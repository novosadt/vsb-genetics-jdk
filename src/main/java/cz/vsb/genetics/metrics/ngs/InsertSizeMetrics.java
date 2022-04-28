package cz.vsb.genetics.metrics.ngs;

import cz.vsb.genetics.ngs.bam.BamUtils;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;

import java.util.ArrayList;
import java.util.List;

public class InsertSizeMetrics {
    public static class InsertSizeInfo {
        private int minInsertSize = Integer.MAX_VALUE;
        private int maxInsertSize = 0;
        private double meanInsertSize = 0.0;
        private double insertSizeStd = 0.0;

        public int getMinInsertSize() {
            return minInsertSize;
        }

        public void setMinInsertSize(int minInsertSize) {
            this.minInsertSize = minInsertSize;
        }

        public int getMaxInsertSize() {
            return maxInsertSize;
        }

        public void setMaxInsertSize(int maxInsertSize) {
            this.maxInsertSize = maxInsertSize;
        }

        public double getMeanInsertSize() {
            return meanInsertSize;
        }

        public void setMeanInsertSize(double meanInsertSize) {
            this.meanInsertSize = meanInsertSize;
        }

        public double getInsertSizeStd() {
            return insertSizeStd;
        }

        public void setInsertSizeStd(double insertSizeStd) {
            this.insertSizeStd = insertSizeStd;
        }
    }

    public InsertSizeInfo estimateInsertSizeMetrics(String bamFile, String indexFile, int estimateReadCount) {
        SamReader reader = BamUtils.getSamReader(bamFile, indexFile);

        List<Integer> insertSizes = new ArrayList<>();
        InsertSizeInfo info = new InsertSizeInfo();
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

        info.insertSizeStd = (double)sum / insertSizes.size();

        return info;
    }
}
