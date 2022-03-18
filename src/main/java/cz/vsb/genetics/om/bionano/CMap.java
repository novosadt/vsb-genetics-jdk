package cz.vsb.genetics.om.bionano;

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
        if (entryPosition.size() == 0)
            return null;

        Map.Entry<Float, CMapEntry> low = ((TreeMap)entryPosition).floorEntry(position);
        Map.Entry<Float, CMapEntry> high = ((TreeMap)entryPosition).ceilingEntry(position);

        if (low != null && high != null)
            return Math.abs(position - low.getKey()) < Math.abs(position - high.getKey())
                    ?   low.getValue()
                    :   high.getValue();


        return  low != null ? low.getValue() : high.getValue();
    }

    public List<CMapEntry> findEntriesAtInterval(int start, int end) {
        if (entryPosition.size() == 0)
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
