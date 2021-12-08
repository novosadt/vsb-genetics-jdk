package cz.vsb.genetics.om.bionano;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CMap {
    private final Integer id;
    private final Map<Integer, CMapEntry> entries = new HashMap<>();

    public CMap(final Integer id) {
        assert id != null;

        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void add(CMapEntry entry) {
        entries.put(entry.getSiteId(), entry);
    }

    public void addAll(Collection<CMapEntry> entries) {
        entries.forEach(entry -> this.entries.put(entry.getSiteId(), entry));
    }

    public void remove(CMapEntry entry) {
        entries.remove(entry.getSiteId());
    }

    public void clear() {
        entries.clear();
    }

    public CMapEntry getEntry(Integer siteId) {
        return entries.get(siteId);
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
