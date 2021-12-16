package cz.vsb.genetics.coverage.om;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.om.bionano.*;

import java.util.List;

public class BionanoCoverageInfo implements CoverageInfo {
    private final String cmapReferenceFile;
    private final String cmapQueryFile;
    private final String xmapFile;
    private CMapContainer cmapContainerRef;
    private CMapContainer cmapContainerQry;
    private XMap xmap;

    public BionanoCoverageInfo(String cmapReferenceFile, String cmapQueryFile, String xmapFile) {
        this.cmapReferenceFile = cmapReferenceFile;
        this.cmapQueryFile = cmapQueryFile;
        this.xmapFile = xmapFile;
    }

    @Override
    public void open() throws Exception {
        cmapContainerRef = new CMapParser().parse(cmapReferenceFile);
        cmapContainerQry = new CMapParser().parse(cmapQueryFile);
        xmap = new XMapParser().parse(xmapFile);
        xmap.findQuerySitesForReferenceSite();
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public long getCoverage(Chromosome chromosome, int position) {
        CMap chromosomeCMap = cmapContainer.get(chromosome.number);
        CMapEntry entry = chromosomeCMap.findNearestEntry(new Float(position));

        return xmap.getSiteCoverage(chromosome.number, entry.getSiteId());
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
