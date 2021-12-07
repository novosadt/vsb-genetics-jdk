package cz.vsb.genetics.om.bionano;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CMap {
    private final Integer id;
    private final List<CMapEntry> entries = new ArrayList<>();

    public CMap(final Integer id) {
        assert id != null;

        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void add(CMapEntry entry) {
        entries.add(entry);
    }

    public void addAll(Collection<CMapEntry> entries) {
        this.entries.addAll(entries);
    }

    public void remove(CMapEntry entry) {
        entries.remove(entry);
    }

    public void clear() {
        entries.clear();
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
