package cz.vsb.genetics.ngs.metrics;

import htsjdk.samtools.SAMRecord;

import java.util.List;

public class ReadCountInfo {
    public List<SAMRecord> splitNormal;
    public List<SAMRecord> splitNormalSupporting;
    public List<SAMRecord> anomalous;
    public int splitNormalBasesOverlap = 0;
    public int splitNormalSupportingBasesOverlap = 0;
}
