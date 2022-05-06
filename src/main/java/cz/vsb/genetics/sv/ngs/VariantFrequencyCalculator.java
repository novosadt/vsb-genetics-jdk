package cz.vsb.genetics.sv.ngs;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.metrics.ngs.ReadInsertSizeInfo;
import cz.vsb.genetics.metrics.ngs.ReadSizeMetrics;
import cz.vsb.genetics.ngs.bam.BamUtils;
import cz.vsb.genetics.sv.StructuralVariant;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.CloseableIterator;

import java.util.*;

public class VariantFrequencyCalculator {
    // number of base-pairs tolerance threshold which we allow breaks to be inexact.
    private int threshold = 6;

    // minimum number of base-pairs a "normal" read must overlap break to be counted.
    private int normalReadOverlap = 10;

    // minimum number of base-pairs a supporting read must be soft-clipped over the break.
    private int supportReadSoftClipLength = 10;

    // number of aligned reads used for estimation of max, min, mean and std insert size
    private int insertSizeEstimateReadCount = 50000;

    private int readLengthEstimateReadCount = 100;

    // mean coverage of the bam file
    private int meanCoverage = 50;

    // maximum considered copy number
    private int maxCopyNumber = 10;

    public void calculateVariantFrequency(List<StructuralVariant> variants, String bamFile, String indexFile) throws Exception {
        ReadInsertSizeInfo insertSizeInfo = ReadSizeMetrics.estimateInsertSizeMetrics(bamFile, indexFile,
                insertSizeEstimateReadCount);

        int readLength = ReadSizeMetrics.estimateReadLength(bamFile, indexFile, readLengthEstimateReadCount);
        int maxInsert = (int)insertSizeInfo.getMeanInsertSize() + (int)(insertSizeInfo.getInsertSizeStd() * 3.0);
        int minInsert = readLength * 2;
        int maxDepth = ((meanCoverage * maxInsert * 2) / readLength) * maxCopyNumber;

        SamReader samReader = BamUtils.getSamReader(bamFile, indexFile);

        samReader.close();
    }

    private double calculateVariantFrequency(SamReader samReader, StructuralVariant variant, ReadInsertSizeInfo insertSizeInfo,
                                             int readLength, int maxInsert, int minInsert, int maxDepth) {
        int srcAlignmentsStart = variant.getSrcLoc().intValue() - maxInsert;
        int srcAlignmentsEnd = variant.getSrcLoc().intValue() + maxInsert;
        int dstAlignmentsStart = variant.getSrcLoc().intValue() - maxInsert;
        int dstAlignmentsEnd = variant.getSrcLoc().intValue() + maxInsert;

        List<SAMRecord> srcReads = getReadsInInterval(samReader, variant.getSrcChromosome(), srcAlignmentsStart, srcAlignmentsEnd);
        List<SAMRecord> dstReads = getReadsInInterval(samReader, variant.getSrcChromosome(), dstAlignmentsStart, dstAlignmentsEnd);



        return 0.0;
    }

    private List<SAMRecord> getReadsInInterval(SamReader samReader, Chromosome chromosome, int start, int end) {
        CloseableIterator<SAMRecord> iterator = samReader.query(chromosome.name(), start, end, true);
        Set<SAMRecord> uniqueAlignments = new HashSet<>();
        while (iterator.hasNext()) {
            SAMRecord record = iterator.next();

            if (record.getReadUnmappedFlag())
                continue;

            uniqueAlignments.add(record);
        }

        List<SAMRecord> alignments = new ArrayList<>(uniqueAlignments);
        Collections.sort(alignments, Comparator.comparing(SAMRecord::getReadName)
                .thenComparing(record -> record.getReferencePositionAtReadPosition(record.getAlignmentStart())));

        return alignments;
    }

    private void getReadCountsAtPosition(List<SAMRecord> reads, int position, int minInsert, int maxInsert) {
        for (int i = 0; i < reads.size() - 1; i++) {
            SAMRecord read1 = reads.get(i);
            SAMRecord read2 = reads.get(i+1);




        }
    }


}
