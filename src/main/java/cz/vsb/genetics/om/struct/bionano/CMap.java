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

import java.util.*;

public class CMap {
    private final Integer id;
    private final Map<Integer, CMapEntry> entries = new HashMap<>();
    private final Map<Float, CMapEntry> entryPosition = new TreeMap<>();

    public CMap(final Integer id) {
        assert id != null;

        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void add(CMapEntry entry) {
        entries.put(entry.getSiteId(), entry);
        entryPosition.put(entry.getPosition(), entry);
    }

    public void addAll(Collection<CMapEntry> entries) {
        entries.forEach(entry -> add(entry));
    }

    public CMapEntry getEntry(Integer siteId) {
        return entries.get(siteId);
    }

    public CMapEntry findNearestEntry(float position) {
        if (entryPosition.isEmpty())
            return null;

        Map.Entry<Float, CMapEntry> low = ((TreeMap<Float, CMapEntry>)entryPosition).floorEntry(position);
        Map.Entry<Float, CMapEntry> high = ((TreeMap<Float, CMapEntry>)entryPosition).ceilingEntry(position);

        if (low != null && high != null)
            return Math.abs(position - low.getKey()) < Math.abs(position - high.getKey())
                    ?   low.getValue()
                    :   high.getValue();


        return  low != null ? low.getValue() : high.getValue();
    }

    public List<CMapEntry> findEntriesAtInterval(int start, int end) {
        if (entryPosition.isEmpty())
            return null;

        CMapEntry startEntry = findNearestEntry(start);
        CMapEntry endEntry = findNearestEntry(end);

        List<CMapEntry> entries = new ArrayList<>(entryPosition.values());

        return entries.subList(entries.indexOf(startEntry), entries.indexOf(endEntry));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CMap)) return false;

        CMap cMap = (CMap) o;

        return id.equals(cMap.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
