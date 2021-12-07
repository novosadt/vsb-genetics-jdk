package cz.vsb.genetics.om.bionano;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class SMap {
    private final Set<SMapEntry> entries = new LinkedHashSet<>();

    public void add(SMapEntry entry) {
        entries.add(entry);
    }

    public Collection<SMapEntry> getEntries() {
        return new LinkedHashSet<>(entries);
    }
}
