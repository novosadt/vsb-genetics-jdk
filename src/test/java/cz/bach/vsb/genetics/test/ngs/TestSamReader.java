package cz.bach.vsb.genetics.test.ngs;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.ngs.bam.BamUtils;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.CloseableIterator;

import java.util.*;

public class TestSamReader {
    public static void main(String[] args) {

    }

    public void testSamReaderQuery(String bamFile, String bamIndexFile, Chromosome chromosome, int position) {
        SamReader samReader = BamUtils.getSamReader(bamFile, bamIndexFile);

        CloseableIterator<SAMRecord> iterator = samReader.query(chromosome.name(), position, position, true);
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
    }
}
