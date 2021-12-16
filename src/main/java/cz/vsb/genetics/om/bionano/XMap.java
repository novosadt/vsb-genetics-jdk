package cz.vsb.genetics.om.bionano;

import java.util.*;

public class XMap {
    private final List<XMapEntry> entries = new ArrayList<>();
    private final Map<Integer, List<Integer>> refSiteToQuerySites = new HashMap<>();

    public void add(XMapEntry entry) {
        entries.add(entry);
    }

    public List<XMapEntry> getEntries() {
        return entries;
    }

    public Integer getSiteCoverage(Integer contigId, Integer siteId) {
        // TODO
        // Na zaklade reference site id si vytahnout prislusne query site ids z refSiteToQuerySites (bude jich tolik,
        // kolik ma coverage reference cmap site id. Nasledne si query cmap pro tyto query site ids vytahnout prislusne
        // coverage a ty secist dohromady. Vysledek da pokryti na dane pozici (siteId) na prislusnem
        // chromozomu (contigId).

        return null;
    }

    public void findQuerySitesForReferenceSite() {
        for (XMapEntry entry : entries) {
            for (XMapEntry.XMapAlignmentEntry alignmentEntry : entry.getAlignments()) {
                List<Integer> querySites = refSiteToQuerySites.get(alignmentEntry.getRefContigSiteId());
                if (querySites == null) {
                    querySites = new ArrayList<>();
                    refSiteToQuerySites.put(alignmentEntry.getRefContigSiteId(), querySites);
                }

                querySites.add(alignmentEntry.getQryContigSiteId());
            }
        }
    }
}
