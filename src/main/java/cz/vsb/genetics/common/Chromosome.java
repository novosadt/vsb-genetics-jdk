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


package cz.vsb.genetics.common;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum Chromosome {
    chr1(1),
    chr2(2),
    chr3(3),
    chr4(4),
    chr5(5),
    chr6(6),
    chr7(7),
    chr8(8),
    chr9(9),
    chr10(10),
    chr11(11),
    chr12(12),
    chr13(13),
    chr14(14),
    chr15(15),
    chr16(16),
    chr17(17),
    chr18(18),
    chr19(19),
    chr20(20),
    chr21(21),
    chr22(22),
    chrX(23),
    chrY(24),
    chrM(25);

    public final int number;

    private static final Map<Integer, Chromosome> map = new HashMap<>();

    static
    {
        for (Chromosome item : Chromosome.values())
            map.put(item.number, item);
    }

    Chromosome(int number) {
        this.number = number;
    }

    public static Chromosome of(Integer number) {
        return map.get(number);
    }
    
    public static Chromosome of(String label) {
        if (StringUtils.isBlank(label))
            return null;

        label = label.toLowerCase();
        label = label.replaceAll("chr", "");

        if (label.equals("mt") || label.equals("m") || label.equals("25"))
            return chrM;

        if (label.equals("y") || label.equals("24"))
            return chrY;

        if (label.equals("x") || label.equals("23"))
            return chrX;

        if (label.equals("22"))
            return chr22;

        if (label.equals("21"))
            return chr21;

        if (label.equals("20"))
            return chr20;

        if (label.equals("19"))
            return chr19;

        if (label.equals("18"))
            return chr18;

        if (label.equals("17"))
            return chr17;

        if (label.equals("16"))
            return chr16;

        if (label.equals("15"))
            return chr15;

        if (label.equals("14"))
            return chr14;

        if (label.equals("13"))
            return chr13;

        if (label.equals("12"))
            return chr12;

        if (label.equals("11"))
            return chr11;

        if (label.equals("10"))
            return chr10;

        if (label.equals("9"))
            return chr9;

        if (label.equals("8"))
            return chr8;

        if (label.equals("7"))
            return chr7;

        if (label.equals("6"))
            return chr6;

        if (label.equals("5"))
            return chr5;

        if (label.equals("4"))
            return chr4;

        if (label.equals("3"))
            return chr3;

        if (label.equals("2"))
            return chr2;

        if (label.equals("1"))
            return chr1;

        return null;
    }
}
