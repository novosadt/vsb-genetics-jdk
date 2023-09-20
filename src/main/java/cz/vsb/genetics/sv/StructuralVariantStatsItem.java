package cz.vsb.genetics.sv;

import java.util.HashMap;
import java.util.Map;

public class StructuralVariantStatsItem {
    private String name;
    private StructuralVariantType svType;
    private final Map<Integer, Integer> distanceVarianceCounts = new HashMap<>();
    int svCountTotal = 0;

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

    public void setName(String name) {
        this.name = name;
    }

    public StructuralVariantType getSvType() {
        return svType;
    }

    public void setSvType(StructuralVariantType svType) {
        this.svType = svType;
    }

    public int getSvCountTotal() {
        return svCountTotal;
    }

    public void setSvCountTotal(int svCountTotal) {
        this.svCountTotal = svCountTotal;
    }
}
