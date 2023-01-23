/*
 * Copyright (C) 2021  Tomas Novosad
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
    
    Chromosome(int number) {
        this.number = number;
    }
    
    public static Chromosome getChromosome(String label) {
        if (StringUtils.isBlank(label))
            return null;

        label = label.toLowerCase();
        label = label.replaceAll("chr", "");

        if (label.contains("mt") || label.contains("m") || label.contains("25"))
            return chrM;

        if (label.contains("y") || label.contains("24"))
            return chrY;

        if (label.contains("x") || label.contains("23"))
            return chrX;

        if (label.contains("22"))
            return chr22;

        if (label.contains("21"))
            return chr21;

        if (label.contains("20"))
            return chr20;

        if (label.contains("19"))
            return chr19;

        if (label.contains("18"))
            return chr18;

        if (label.contains("17"))
            return chr17;

        if (label.contains("16"))
            return chr16;

        if (label.contains("15"))
            return chr15;

        if (label.contains("14"))
            return chr14;

        if (label.contains("13"))
            return chr13;

        if (label.contains("12"))
            return chr12;

        if (label.contains("11"))
            return chr11;

        if (label.contains("10"))
            return chr10;

        if (label.contains("9"))
            return chr9;

        if (label.contains("8"))
            return chr8;

        if (label.contains("7"))
            return chr7;

        if (label.contains("6"))
            return chr6;

        if (label.contains("5"))
            return chr5;

        if (label.contains("4"))
            return chr4;

        if (label.contains("3"))
            return chr3;

        if (label.contains("2"))
            return chr2;

        if (label.contains("1"))
            return chr1;

        return null;
    }
}
