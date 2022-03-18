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


package cz.vsb.genetics.coverage.om;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.om.bionano.*;

import java.util.ArrayList;
import java.util.List;

public class BionanoCoverageInfo implements CoverageInfo {
    private final String cmapReferenceFile;
    private final String cmapQueryFile;
    private final String xmapFile;
    private CMapContainer cmapContainerRef;
    private XMap xmap;

    public BionanoCoverageInfo(String cmapReferenceFile, String cmapQueryFile, String xmapFile) {
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
    public long getCoverage(Chromosome chromosome, int position) {
        CMap chromosomeCMap = cmapContainerRef.get(chromosome.number);
        CMapEntry entry = chromosomeCMap.findNearestEntry(position);

        return xmap.getSiteCoverage(chromosome.number, entry.getSiteId());
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end) {
        List<Long> coverages = new ArrayList<>();

        for (int i = start; i <= end; i++)
            coverages.add(getCoverage(chromosome, i));

        return coverages;
    }

    @Override
    public List<Long> getCoverage(Chromosome chromosome, int start, int end, int step) {
        CMap chromosomeCMap = cmapContainerRef.get(chromosome.number);
        List<CMapEntry> entries = chromosomeCMap.findEntriesAtInterval(start, end);
        List<Long> coverages = new ArrayList<>();

        for (int i = 0; i < entries.size(); i += step)
            coverages.add(xmap.getSiteCoverage(chromosome.number, entries.get(i).getSiteId()));

        return coverages;
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end) {
        long total = 0;

        List<Long> coverages = getCoverage(chromosome, start, end);

        for (Long coverage : coverages)
            total += coverage;

        return (double)total / (double)coverages.size();
    }

    @Override
    public double getMeanCoverage(Chromosome chromosome, int start, int end, int step) {
        long total = 0;

        List<Long> coverages = getCoverage(chromosome, start, end, step);

        for (Long coverage : coverages)
            total += coverage;

        return (double)total / (double)coverages.size();
    }
}
