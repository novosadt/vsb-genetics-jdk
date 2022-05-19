package cz.vsb.genetics.om.struct.bionano;

import java.util.HashMap;
import java.util.Map;

public class CMapContainer {
    private final Map<Integer, CMap> cmaps = new HashMap<>();

    public void add(CMap cmap) {
        cmaps.put(cmap.getId(), cmap);
    }

    public CMap get(Integer cmapId) {
        return cmaps.get(cmapId);
    }

}
