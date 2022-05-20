package cz.vsb.genetics.ngs.metrics;

import htsjdk.samtools.SAMRecord;

import java.util.List;

public class ReadCountInfo {
    public List<SAMRecord> splitNormal;
    public List<SAMRecord> splitNormalSupporting;
    public int splitNormalCount = 0;
    public int splitNormalBasesOverlap = 0;
    public int splitNormalSupportingCount = 0;
    public int splitNormalSupportingBasesOverlap = 0;

}
