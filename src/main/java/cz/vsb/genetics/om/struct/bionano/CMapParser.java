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

        CMapEntry entry = new CMapEntry(Integer.valueOf(values[0]), Integer.valueOf(values[3]));

        entry.setContigLength(getArrayFloatValue(values, 1));
        entry.setNumSites(Integer.valueOf(values[2]));
        entry.setLabelChannel(Integer.valueOf(values[4]));
        entry.setPosition(getArrayFloatValue(values, 5));
        entry.setStdDev(getArrayFloatValue(values, 6));
        entry.setCoverage(getArrayFloatValue(values, 7));
        entry.setOccurrence(getArrayFloatValue(values, 8));
        entry.setChimQuality(getArrayFloatValue(values, 9));
        entry.setSegDupL(getArrayFloatValue(values, 10));
        entry.setSegDupR(getArrayFloatValue(values, 11));
        entry.setFragileL(getArrayFloatValue(values, 12));
        entry.setFragileR(getArrayFloatValue(values, 13));
        entry.setOutlierFrac(getArrayFloatValue(values, 14));
        entry.setChimNorm(getArrayFloatValue(values, 15));

        return entry;
    }
    
    private Float getArrayFloatValue(String[] array, int index) {
        if (index >= array.length)
            return null;
        
        return Float.valueOf(array[index]);
    }
}
