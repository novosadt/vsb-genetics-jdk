package cz.bach.vsb.genetics.test.ngs;/*
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

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.ngs.BamCoverageInfo;
import cz.vsb.genetics.coverage.ngs.BamCoverageInfoMT;
import cz.vsb.genetics.coverage.ngs.BamCoverageInfoST;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

public class TestBamCoverage {
    public static void main(String[] args) {
        try {
            Chromosome chromosome = Chromosome.chr17;
            int start = 7675023;
            int end = 7675999;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            //testBamCoverageAtPosition(chromosome, start);
            //testBamCoverageAtIntervalST(chromosome, start, end);
            testBamCoverageAtIntervalMT(chromosome, start, end, 6);

            stopWatch.stop();
            System.out.println("Time (mils): " + stopWatch.getTime());
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void testBamCoverageAtPosition(Chromosome chromosome, int position) throws Exception {
        String bamFile = "./data/ngs/bam/test.bam";
        String indexFile = "./data/ngs/bam/test.bai";

        BamCoverageInfoST bamCoverageInfo = new BamCoverageInfoST(bamFile, indexFile);
        long coverage = bamCoverageInfo.getCoverage(chromosome, position);

        String info = String.format("Coverage at position %s:%d - %d", chromosome.toString(), position, coverage);

        System.out.println(info);

        bamCoverageInfo.close();
    }

    public static void testBamCoverageAtIntervalST(Chromosome chromosome, int start, int end) throws Exception {
        String bamFile = "./data/ngs/bam/test.bam";
        String indexFile = "./data/ngs/bam/test.bai";

        BamCoverageInfo bamCoverageInfo = new BamCoverageInfoST(bamFile, indexFile);
        List<Long> coverages = bamCoverageInfo.getCoverage(chromosome, start, end);

        for (Long coverage : coverages)
            System.out.println(String.format("Coverage at position %s:%d - %d", chromosome.toString(), start++, coverage));

        bamCoverageInfo.close();
    }

    public static void testBamCoverageAtIntervalMT(Chromosome chromosome, int start, int end, int threads) throws Exception {
        String bamFile = "./data/ngs/bam/test.bam";
        String indexFile = "./data/ngs/bam/test.bai";

        BamCoverageInfo bamCoverageInfo = new BamCoverageInfoMT(bamFile, indexFile, threads);
        List<Long> coverages = bamCoverageInfo.getCoverage(chromosome, start, end);

        for (Long coverage : coverages)
            System.out.println(String.format("Coverage at position %s:%d - %d", chromosome.toString(), start++, coverage));

        bamCoverageInfo.close();
    }
}
