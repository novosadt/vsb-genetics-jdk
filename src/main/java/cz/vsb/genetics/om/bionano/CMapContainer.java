package cz.vsb.genetics.om.bionano;

import java.util.HashMap;
import java.util.Map;

public class CMapContainer {
    private final Map<Integer, CMap> cmaps = new HashMap<>();

    public void add(CMap cmap) {
        cmaps.put(cmap.getId(), cmap);
    }


}
