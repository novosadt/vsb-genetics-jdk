package cz.vsb.genetics.om.bionano;

import java.util.*;

public class XMap {
    private final List<XMapEntry> entries = new ArrayList<>();
    private Map<Integer, Map<Integer, Integer>> contigSiteAlignmentHits = new HashMap<>();

    public void add(XMapEntry entry) {
        entries.add(entry);
    }

    public List<XMapEntry> getEntries() {
        return entries;
    }

    public Map<Integer, Map<Integer, Integer>> getContigSiteAlignmentHits() {
        return contigSiteAlignmentHits;
    }

    public void calculateContigSiteAlignmentHits() {
        contigSiteAlignmentHits.clear();

        for (XMapEntry entry : entries) {
            Map<Integer, Integer> siteAlignmentHits = contigSiteAlignmentHits.get(entry.getRefContigID());
            if (siteAlignmentHits == null) {
                siteAlignmentHits = new HashMap<>();
                contigSiteAlignmentHits.put(entry.getRefContigID(), siteAlignmentHits);
            }

            for (XMapEntry.XMapAlignmentEntry alignmentEntry : entry.getAlignments()) {
                Integer refSiteId = alignmentEntry.getRefContigSiteId();
                Integer alignmentHits = siteAlignmentHits.get(refSiteId);
                if (alignmentHits == null) {
                    alignmentHits = 0;
                    siteAlignmentHits.put(refSiteId, alignmentHits);
                }

                siteAlignmentHits.put(refSiteId, ++alignmentHits);
            }
        }
    }
}
