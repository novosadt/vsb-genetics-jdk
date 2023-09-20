package cz.vsb.genetics.sv;

import java.util.HashMap;
import java.util.Map;

public class StructuralVariantStatsItem {
    private final String name;
    private final StructuralVariantType svType;
    private final Map<Integer, Integer> distanceVarianceCounts = new HashMap<>();
    int svCountTotal = 0;

    public StructuralVariantStatsItem(String name, StructuralVariantType svType, int svCountTotal) {
        this.name = name;
        this.svType = svType;
        this.svCountTotal = svCountTotal;
    }

    public void addStructuralVariant(int distanceVariance) {
        Integer count = distanceVarianceCounts.get(distanceVariance);

        if (count == null)
            count = 0;

        distanceVarianceCounts.put(distanceVariance, ++count);
    }

    public int getSvCounts(int distanceVariance) {
        Integer count = distanceVarianceCounts.get(distanceVariance);

        return count == null ? 0 : count;
    }

    public String getName() {
        return name;
    }

    public StructuralVariantType getSvType() {
        return svType;
    }

    public int getSvCountTotal() {
        return svCountTotal;
    }

    public void setSvCountTotal(int svCountTotal) {
        this.svCountTotal = svCountTotal;
    }
}
