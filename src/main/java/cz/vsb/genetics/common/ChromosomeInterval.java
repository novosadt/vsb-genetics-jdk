package cz.vsb.genetics.common;

public class ChromosomeInterval {
    private final Chromosome chromosome;
    private final Long start;
    private final Long end;

    public ChromosomeInterval(Chromosome chromosome, Long start, Long end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }
}
