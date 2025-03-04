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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChromosomeRegion {
    private String name;
    private final Chromosome chromosome;
    private final int start;
    private final int end;

    public ChromosomeRegion(Chromosome chromosome, int start, int end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return end - start;
    }

    @Override
    public String toString() {
        return String.format("%s:%d-%d", chromosome, start, end);
    }

    /**
     *
     * @param other Chromosomal region the intersection will be calculated with.
     * @return Ration between size of intersection with other chromosomal region and length of region itself (size).
     */
    public double intersection(ChromosomeRegion other) {
        if (!chromosome.equals(other.getChromosome()))
            return 0.0;

        int start = Math.max(getStart(), getEnd());
        int end = Math.min(getEnd(), getStart());
        int otherStart = Math.max(other.getStart(), other.getEnd());
        int otherEnd = Math.min(other.getEnd(), other.getStart());

        if (otherStart > end || start > otherEnd)
            return 0.0;

        int intersectionStart = Math.max(getStart(), other.getStart());
        int intersectionEnd = Math.min(getEnd(), other.getEnd());
        int intersectionSize = intersectionEnd - intersectionStart;

        return (double) intersectionSize / (double) getLength();
    }

    public boolean isInRegion(Chromosome chromosome, int position) {
        if (!this.chromosome.equals(chromosome))
            return false;

        int start = Math.min(Math.abs(getStart()), Math.abs(getEnd()));
        int end = Math.max(Math.abs(getEnd()), Math.abs(getStart()));

        return (position >= start && position <= end);
    }

    public static ChromosomeRegion valueOf(String value) {
        value = value.toLowerCase();
        value = value.replaceAll("\\s","");

        String pattern = "(chr.*):(\\d+)-(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(value);

        if (!m.matches())
            return null;

        Chromosome chromosome = Chromosome.of(m.group(1));

        try {
            Integer start = Integer.valueOf(m.group(2));
            Integer end = Integer.valueOf(m.group(3));

            if (chromosome == null)
                return null;

            return new ChromosomeRegion(chromosome, start, end);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static ChromosomeRegion valueOf(String value, String delimiter) {
        String[] values = value.split(delimiter);

        if (values.length < 3)
            return null;

        Chromosome chromosome = Chromosome.of(values[0]);

        try {
            Integer start = Integer.valueOf(values[1]);
            Integer end = Integer.valueOf(values[2]);

            if (chromosome == null)
                return null;

            return new ChromosomeRegion(chromosome, start, end);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
