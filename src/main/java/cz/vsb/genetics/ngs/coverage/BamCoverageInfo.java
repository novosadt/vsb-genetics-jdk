package cz.vsb.genetics.ngs.coverage;

import cz.vsb.genetics.common.Chromosome;

import java.util.List;

public interface BamCoverageInfo {
    void close() throws Exception;

    long getCoverage(Chromosome chromosome, int position);

    List<Long> getCoverage(Chromosome chromosome, int start, int end) throws Exception;

    List<Long> getCoverage(Chromosome chromosome, int start, int end, int step) throws Exception;

    double getMeanCoverage(Chromosome chromosome, int start, int end) throws Exception;

    double getMeanCoverage(Chromosome chromosome, int start, int end, int step) throws Exception;
}
