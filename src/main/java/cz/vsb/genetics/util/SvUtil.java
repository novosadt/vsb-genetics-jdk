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


package cz.vsb.genetics.util;

import cz.vsb.genetics.sv.StructuralVariant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SvUtil {
    public static String getVariantInfo(StructuralVariant structuralVariant, String[] infoTags) {
        List<String> infos = new ArrayList<>();

        for (String infoTag : infoTags) {
            String info = structuralVariant.getInfo().get(infoTag);

            if (StringUtils.isNotBlank(info))
                infos.add(info);
        }

        return StringUtils.join(infos, ";");
    }

    public static String idsToString(List<StructuralVariant> variants, String[] infoTags) {
        if (variants.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        for (StructuralVariant variant : variants) {
            String info = getVariantInfo(variant, infoTags);
            String id = StringUtils.isBlank(info) ? variant.getId() : variant.getId() + ":" + info;

            sb.append(id + ";");
        }

        return StringUtils.chop(sb.toString());
    }

    public static int distanceVariance(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        int srcDist = Math.abs(structuralVariant.getSrcLoc() - otherVariant.getSrcLoc());
        int dstDist = Math.abs(structuralVariant.getDstLoc() - otherVariant.getDstLoc());
        int distanceVariance = srcDist + dstDist;

        return distanceVariance;
    }

    public static double intersectionVariance(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        int from = Math.min(structuralVariant.getSrcLoc(), otherVariant.getSrcLoc());
        int to = Math.max(structuralVariant.getDstLoc(), otherVariant.getDstLoc());
        double intersectionVariance = (double)(to - from + 1) / (double)(structuralVariant.getSize() + otherVariant.getSize());

        //Value of 0.5 means absolute match of variants. So just "normalize" it to 0.0 if there is absolute match.
        //Value of 0.5 for absolute match may be misleading
        return intersectionVariance - 0.5;
    }

    public static Double sizeProportion(StructuralVariant main, StructuralVariant other) {
        if (main.getSize() == 0 || other.getSize() == 0)
            return null;

        double numerator = Math.min(main.getSize(), other.getSize());
        double denominator = Math.max(main.getSize(), other.getSize());

        return numerator / denominator;
    }
}
