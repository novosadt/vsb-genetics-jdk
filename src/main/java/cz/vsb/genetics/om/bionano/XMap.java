package cz.vsb.genetics.om.bionano;

import java.util.LinkedHashSet;
import java.util.Set;

public class XMap {
    private final Set<XMapEntry> entries = new LinkedHashSet<>();

    public void add(XMapEntry entry) {
        entries.add(entry);
    }
}
