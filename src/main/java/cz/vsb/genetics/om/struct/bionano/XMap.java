/*
 * Copyright (C) 2025  Tomas Novosad
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


package cz.vsb.genetics.om.struct.bionano;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMap {
    private final List<XMapEntry> entries = new ArrayList<>();
    private final Map<Integer, Map<Integer, List<XMapEntry.XMapAlignmentEntry>>> refSiteToQuerySites = new HashMap<>();
    private CMapContainer queryCMapContainer;

    public void add(XMapEntry entry) {
        entries.add(entry);
    }

    public List<XMapEntry> getEntries() {
        return entries;
    }

    public int getSiteCoverage(Integer contigId, Integer siteId) {
        Map<Integer, List<XMapEntry.XMapAlignmentEntry>> contigSites = refSiteToQuerySites.get(contigId);

        if (contigSites == null)
            return 0;

        List<XMapEntry.XMapAlignmentEntry> alignments = contigSites.get(siteId);

        if (alignments == null)
            return 0;

        int coverage = 0;

        for (XMapEntry.XMapAlignmentEntry alignment : alignments) {
            CMap queryCMap = queryCMapContainer.get(alignment.getQryContigId());
            CMapEntry entry = queryCMap.getEntry(alignment.getQryContigSiteId());
            coverage += entry.getCoverage();
        }

        return coverage;
    }

    public void findQuerySitesForReferenceSite() {
        for (XMapEntry entry : entries) {
            for (XMapEntry.XMapAlignmentEntry alignmentEntry : entry.getAlignments()) {
                Map<Integer, List<XMapEntry.XMapAlignmentEntry>> refContigSites = refSiteToQuerySites.get(alignmentEntry.getRefContigId());

                if (refContigSites == null) {
                    refContigSites = new HashMap<>();
                    refSiteToQuerySites.put(alignmentEntry.getRefContigId(), refContigSites);
                }

                List<XMapEntry.XMapAlignmentEntry> querySites = refContigSites.get(alignmentEntry.getRefContigSiteId());
                if (querySites == null) {
                    querySites = new ArrayList<>();
                    refContigSites.put(alignmentEntry.getRefContigSiteId(), querySites);
                }

                querySites.add(alignmentEntry);
            }
        }
    }

    public void setQueryCMapContainer(CMapContainer queryCMapContainer) {
        this.queryCMapContainer = queryCMapContainer;
    }
}
