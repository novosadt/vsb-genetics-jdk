package cz.vsb.genetics.om.bionano;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CMapParser {
    public CMapContainer parse(String file) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<Integer, List<CMapEntry>> cmapsEntries = new TreeMap<>();

        String line;
        while((line = reader.readLine()) != null) {
            if (line.startsWith("#") || StringUtils.isBlank(line))
                continue;

            CMapEntry entry = parseLine(line);

            List<CMapEntry> entries = cmapsEntries.get(entry.getCmapId());

            if (entries == null) {
                entries = new ArrayList<>();
                cmapsEntries.put(entry.getCmapId(), entries);
            }

            entries.add(entry);
        }

        reader.close();

        CMapContainer container = new CMapContainer();

        for (Integer cmapId : cmapsEntries.keySet()) {
            List<CMapEntry> entries = cmapsEntries.get(cmapId);
            CMap cmap = new CMap(cmapId);
            cmap.addAll(entries);

            container.add(cmap);
        }

        return container;
    }

    private CMapEntry parseLine(String line) {
        String[] values = line.split("\t");

        int l = values.length;
        int i = 0;

        CMapEntry entry = new CMapEntry();
        entry.setCmapId(l < ++i ? null : new Integer(values[0]));
        entry.setContigLength(l < ++i ? null : new Float(values[1]));
        entry.setNumSites(l < ++i ? null : new Integer(values[2]));
        entry.setSiteId(l < ++i ? null : new Integer(values[3]));
        entry.setLabelChannel(l < ++i ? null : new Integer(values[4]));
        entry.setPosition(l < ++i ? null : new Float(values[5]));
        entry.setStdDev(l < ++i ? null : new Float(values[6]));
        entry.setCoverage(l < ++i ? null : new Float(values[7]));
        entry.setOccurrence(l < ++i ? null : new Float(values[8]));
        entry.setChimQuality(l < ++i ? null : new Float(values[9]));
        entry.setSegDupL(l < ++i ? null : new Float(values[10]));
        entry.setSegDupR(l < ++i ? null : new Float(values[11]));
        entry.setFragileL(l < ++i ? null : new Float(values[12]));
        entry.setFragileR(l < ++i ? null : new Float(values[13]));
        entry.setOutlierFrac(l < ++i ? null : new Float(values[14]));
        entry.setChimNorm(l < ++i ? null : new Float(values[15]));

        return entry;
    }
}
