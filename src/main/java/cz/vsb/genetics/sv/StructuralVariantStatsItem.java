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


package cz.vsb.genetics.sv;

import java.util.HashMap;
import java.util.Map;

public class StructuralVariantStatsItem {
    private final String name;
    private final StructuralVariantType svType;
    private final Map<Integer, Integer> distanceVarianceCounts = new HashMap<>();
    private final Map<Double, Integer> intersectionVarianceCounts = new HashMap<>();
    int svCountTotal;
    int svCountPassed = 0;

    public StructuralVariantStatsItem(String name, StructuralVariantType svType) {
        this.name = name;
        this.svType = svType;
    }

    public void addStructuralVariantDistanceVariance(int distanceVariance) {
        Integer count = distanceVarianceCounts.get(distanceVariance);

        if (count == null)
            count = 0;

        distanceVarianceCounts.put(distanceVariance, ++count);
    }

    public int getSvCountsDistanceVariance(int distanceVariance) {
        Integer count = distanceVarianceCounts.get(distanceVariance);

        return count == null ? 0 : count;
    }

    public void addStructuralVariantIntersectionVariance(double intersectionVariance) {
        Integer count = intersectionVarianceCounts.get(intersectionVariance);

        if (count == null)
            count = 0;

        intersectionVarianceCounts.put(intersectionVariance, ++count);
    }

    public int getSvCountsIntersectionVariance(double intersectionVariance) {
        Integer count = intersectionVarianceCounts.get(intersectionVariance);

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

    public int getSvCountPassed() {
        return svCountPassed;
    }

    public void addSvCountPassed() {
        svCountPassed++;
    }
}
