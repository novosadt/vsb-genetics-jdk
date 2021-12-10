package cz.vsb.genetics.coverage.om;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.om.bionano.*;

import java.util.List;

public class BionanoCoverageInfo implements CoverageInfo {
    private final String cmapReferenceFile;
    private final String xmapFile;
    private CMapContainer cmapContainer;
    private XMap xmap;

    public BionanoCoverageInfo(String cmapReferenceFile, String xmapFile) {
        this.cmapReferenceFile = cmapReferenceFile;
        this.xmapFile = xmapFile;
    }

    @Override
    public void open() throws Exception {
        cmapContainer = new CMapParser().parse(cmapReferenceFile);
        xmap = new XMapParser().parse(xmapFile);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public long getCoverage(Chromosome chromosome, int position) {
        return 0;
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end) throws Exception {
        return null;
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end, int step) throws Exception {
        return null;
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) throws Exception {
        return 0;
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end, int step) throws Exception {
        return 0;
    }
}
