package cz.vsb.genetics.common;

import org.apache.commons.lang3.StringUtils;

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

    public static ChromosomeRegion valueOf(String value) {
        value = value.toLowerCase();
        value = value.replaceAll("\\s","");

        String pattern = "(chr.*):(\\d+)-(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(value);

        if (!m.matches())
            return null;

        String chromosome = m.group(1);
        Integer start = Integer.valueOf(m.group(2));
        Integer end = Integer.valueOf(m.group(3));

        if (StringUtils.isBlank(chromosome) || start == null || end == null)
            return null;

        return new ChromosomeRegion(Chromosome.of(chromosome), start, end);
    }
}
