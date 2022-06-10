/*
 * Copyright (C) 2022  Tomas Novosad
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

/*
 * Source code for variant frequency calculation is based on SVClone python
 * package for structural variant frequency calculation.
 */

package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.ngs.metrics.ReadCountInfo;
import cz.vsb.genetics.ngs.metrics.ReadInsertSizeInfo;
import cz.vsb.genetics.ngs.metrics.ReadSizeMetrics;
import cz.vsb.genetics.ngs.bam.BamUtils;
import cz.vsb.genetics.sv.StructuralVariant;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.CloseableIterator;

import java.util.*;

public class VariantFrequencyCalculator {
    // number of base-pairs tolerance threshold which we allow breaks to be inexact.
    private int breakThreshold = 6;

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

    private ReadCountInfo getReadCountsAtPosition(List<SAMRecord> reads, int position, int minInsert, int maxInsert) {
        ReadCountInfo readCountInfo = new ReadCountInfo();

        for (int i = 0; i < reads.size(); i++) {
            SAMRecord read = reads.get(i);
            SAMRecord mate = i + 1 < reads.size() ? reads.get(i + 1) : null;

            if (mate != null && isNormalNonOverlap(read, mate, position, minInsert, maxInsert))
                continue;
            else if (isNormalAcrossBreak(read, position, minInsert, maxInsert)) {
                readCountInfo.splitNormal.add(read);
                readCountInfo.splitNormalBasesOverlap += getNormalOverlapBaseCount(read, position);
            }
            else if (isSupportingSplitRead(read, position, maxInsert)) {
                readCountInfo.splitNormalSupporting.add(read);
                readCountInfo.splitNormalSupportingBasesOverlap += getSoftClippedBasesCount(read);
            }
            else if (mate != null && read.getReadName().equals(mate.getReadName()) &&
                    isNormalSpanning(read, mate, position, minInsert, maxInsert) &&
                    !isNormalAcrossBreak(read, position, minInsert, maxInsert) &&
                    !isNormalAcrossBreak(mate, position, minInsert, maxInsert)) {
                readCountInfo.splitNormal.add(read);

                if (i + 1 == reads.size())
                    readCountInfo.splitNormal.add(mate);
            }
            else
                readCountInfo.anomalous.add(read);
        }

        return readCountInfo;
    }

    private boolean isNormalNonOverlap(SAMRecord read, SAMRecord mate, int position, int minInsert, int maxInsert) {
        if (!read.getContig().equals(mate.getContig()))
            return false;

        int readInsertSize = Math.abs(read.getInferredInsertSize());
        int mateInsertSize = Math.abs(mate.getInferredInsertSize());
        int readRefStart = read.getReferencePositionAtReadPosition(read.getAlignmentStart());
        int readRefEnd = read.getReferencePositionAtReadPosition(read.getAlignmentEnd());
        int mateRefStart = read.getReferencePositionAtReadPosition(mate.getAlignmentStart());
        int mateRefEnd = read.getReferencePositionAtReadPosition(mate.getAlignmentEnd());

        return !isSoftClipped(read) &&
                (readInsertSize < maxInsert && readInsertSize > minInsert) &&
                (mateInsertSize < maxInsert && mateInsertSize > minInsert) &&
                !(readRefStart < (position + breakThreshold) && readRefEnd > (position - breakThreshold)) &&
                !(mateRefStart < (position + breakThreshold) && mateRefEnd > (position - breakThreshold)) &&
                !(readRefStart < position && mateRefEnd > position);
    }

    private boolean isSoftClipped(SAMRecord read) {
        return read.getAlignmentStart() != 0 || read.getAlignmentEnd() != read.getReadLength();
    }
    
    private boolean isNormalAcrossBreak(SAMRecord read, int position, int minInsert, int maxInsert) {
        int insertSize = Math.abs(read.getInferredInsertSize());
        int refStart = read.getReferencePositionAtReadPosition(read.getAlignmentStart());
        int refEnd = read.getReferencePositionAtReadPosition(read.getAlignmentEnd());
        
        return isBelowSoftClippingThreshold(read, 2) &&
                (insertSize < maxInsert && insertSize > minInsert) &&
                (refStart < position - normalReadOverlap) && (refEnd > position + normalReadOverlap);
                
    }
    
    private boolean isBelowSoftClippingThreshold(SAMRecord read, int threshold) {
        return read.getAlignmentStart() < threshold && 
               read.getReadLength() - read.getAlignmentEnd() < threshold;
    }

    private int getNormalOverlapBaseCount(SAMRecord read, int position) {
        int refStart = read.getReferencePositionAtReadPosition(read.getAlignmentStart());
        int refEnd= read.getReferencePositionAtReadPosition(read.getAlignmentEnd());

        return Math.min(Math.abs(refStart - position), Math.abs(refEnd - position));
    }

    // Return whether read is a supporting split read. Doesn't yet check whether the soft-clip aligns to the other side.
    private boolean isSupportingSplitRead(SAMRecord read, int position, int maxInsert) {
        int alignmentStart = read.getAlignmentStart();
        int alignmentEnd = read.getAlignmentEnd();
        int refStart = read.getReferencePositionAtReadPosition(alignmentStart);
        int refEnd = read.getReferencePositionAtReadPosition(alignmentEnd);
        int insertSize = read.getInferredInsertSize();

        if (alignmentStart < breakThreshold)
            return refEnd > (position - breakThreshold) && refEnd < (position + breakThreshold) &&
                    read.getReadLength() - alignmentEnd >= supportReadSoftClipLength &&
                    Math.abs(insertSize) < maxInsert;

        return refStart > (position - breakThreshold) && refStart < (position + breakThreshold) &&
                alignmentStart >= supportReadSoftClipLength &&
                Math.abs(insertSize) < maxInsert;
    }

    private int getSoftClippedBasesCount(SAMRecord read) {
        if (read.getAlignmentStart() < breakThreshold)
            return read.getReadLength() - read.getAlignmentEnd();
        else
            return read.getAlignmentStart();
    }

    private boolean isNormalSpanning(SAMRecord read, SAMRecord mate, int position, int minInsert, int maxInsert) {
        if (!isSoftClipped(read) && !isSoftClipped(mate) && read.getReadNegativeStrandFlag() != mate.getReadNegativeStrandFlag()) {
            return Math.abs(read.getInferredInsertSize()) < maxInsert && Math.abs(read.getInferredInsertSize()) > minInsert &&
                    read.getReferencePositionAtReadPosition(read.getAlignmentStart()) < position + supportReadSoftClipLength &&
                    mate.getReferencePositionAtReadPosition(mate.getAlignmentEnd()) > position - supportReadSoftClipLength;
        }

        return false;

    }
}
