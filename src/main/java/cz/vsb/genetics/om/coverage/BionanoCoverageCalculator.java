/*
 * Copyright (C) 2021  Tomas Novosad
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


package cz.vsb.genetics.om.coverage;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageCalculator;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.om.struct.bionano.*;

import java.util.HashSet;
import java.util.Set;

public class BionanoCoverageCalculator implements CoverageCalculator {
    private final String cmapReferenceFile;
    private final String cmapQueryFile;
    private final String xmapFile;
    private CMapContainer cmapContainerRef;
    private XMap xmap;

    public BionanoCoverageCalculator(String cmapReferenceFile, String cmapQueryFile, String xmapFile) {
        this.cmapReferenceFile = cmapReferenceFile;
        this.cmapQueryFile = cmapQueryFile;
        this.xmapFile = xmapFile;
    }

    @Override
    public void open() throws Exception {
        cmapContainerRef = new CMapParser().parse(cmapReferenceFile);
        CMapContainer cmapContainerQry = new CMapParser().parse(cmapQueryFile);
        xmap = new XMapParser().parse(xmapFile);
        xmap.findQuerySitesForReferenceSite();
        xmap.setQueryCMapContainer(cmapContainerQry);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public int getPositionCoverage(Chromosome chromosome, int position) {
        CMap chromosomeCMap = cmapContainerRef.get(chromosome.number);
        if (chromosomeCMap == null)
            return 0;

        CMapEntry entry = chromosomeCMap.findNearestEntry(position);

        return xmap.getSiteCoverage(chromosome.number, entry.getSiteId());
    }

    @Override
    public CoverageInfo getIntervalCoverage(Chromosome chromosome, int start, int end) {
        int[] coverages = new int[end - start + 1];
        int minCoverage = Integer.MAX_VALUE;
        int maxCoverage = 0;

        for (int i = 0; i < coverages.length; i++) {
            coverages[i] = getPositionCoverage(chromosome, start + i);

            if (coverages[i] > maxCoverage)
                maxCoverage = coverages[i];

            if (coverages[i] < minCoverage)
                minCoverage = coverages[i];
        }

        CoverageInfo coverageInfo = new CoverageInfo(coverages, minCoverage, maxCoverage, start, end);
        coverageInfo.setSiteCount(getIntervalSiteCount(chromosome, start, end));

        return coverageInfo;
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) {
        long total = 0;

        int[] coverages = getIntervalCoverage(chromosome, start, end).getCoverages();

        for (int coverage : coverages)
            total += coverage;

        return (double)total / (double)coverages.length;
    }

    private int getIntervalSiteCount(Chromosome chromosome, int start, int end) {
        CMap chromosomeCMap = cmapContainerRef.get(chromosome.number);

        Set<CMapEntry> entries = new HashSet<>();
        for (int position = start; position <= end; position++) {
            CMapEntry entry = chromosomeCMap.findNearestEntry(position);

            if (entry.getPosition() >= start && entry.getPosition() <= end)
                entries.add(chromosomeCMap.findNearestEntry(position));
        }

        return entries.size();
    }

    @Override
    public void setMappingQuality(int mappingQuality) {
        throw new UnsupportedOperationException("Setting mapping quality for Optical Mapping technologies is not supported yet.");
    }
}
