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


package cz.bach.vsb.genetics.test.ngs;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.coverage.CoverageInfo;
import cz.vsb.genetics.ngs.coverage.BamCoverageInfoMT;
import cz.vsb.genetics.ngs.coverage.BamCoverageInfoST;
import org.apache.commons.lang3.time.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TestBamCoverage {
    private static final String BAM_FILE = "./src/test/resources/coverage/bam/test.bam";
    private static final String BAM_INDEX_FILE = "./src/test/resources/coverage/bam/test.bai";

    public static void main(String[] args) {
        try {
            Chromosome chromosome = Chromosome.chr17;
            int start = 7675023;
            int end = 7675999;
            //int end = 7675043;

            testBamCoverageAtPosition(chromosome, start);
            testBamCoverageAtIntervalST(chromosome, start, end);
            testBamCoverageAtIntervalMT(chromosome, start, end, 6);
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void testBamCoverageAtPosition(Chromosome chromosome, int position) throws Exception {
        System.out.println("\nTesting bam coverage info at position.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        BamCoverageInfoST coverageInfo = new BamCoverageInfoST(BAM_FILE, BAM_INDEX_FILE);
        coverageInfo.open();
        long coverage = coverageInfo.getPositionCoverage(chromosome, position);
        coverageInfo.close();

        stopWatch.stop();

        printTime(stopWatch.getTime());
        System.out.printf("Coverage at position %s:%d - %d\n", chromosome.toString(), position, coverage);
    }

    public static void testBamCoverageAtIntervalST(Chromosome chromosome, int start, int end) throws Exception {
        System.out.println("\nTesting bam coverage info at interval - single-threaded.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CoverageInfo coverageInfo = new BamCoverageInfoST(BAM_FILE, BAM_INDEX_FILE);
        coverageInfo.open();
        long[] coverages = coverageInfo.getIntervalCoverage(chromosome, start, end);
        coverageInfo.close();

        stopWatch.stop();
        printTime(stopWatch.getTime());

        for (Long coverage : coverages)
            System.out.printf("Coverage at position %s:%d - %d%n", chromosome.toString(), start++, coverage);
    }

    public static void testBamCoverageAtIntervalMT(Chromosome chromosome, int start, int end, int threads) throws Exception {
        System.out.println("\nTesting bam coverage info at interval - multi-threaded.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CoverageInfo coverageInfo = new BamCoverageInfoMT(BAM_FILE, BAM_INDEX_FILE, threads);
        coverageInfo.open();
        long[] coverages = coverageInfo.getIntervalCoverage(chromosome, start, end);
        coverageInfo.close();

        stopWatch.stop();
        printTime(stopWatch.getTime());

        for (Long coverage : coverages)
            System.out.printf("Coverage at position %s:%d - %d%n", chromosome.toString(), start++, coverage);
    }

    private static void printTime(long mils) {
        Date time = new Date(mils);

        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("Time elapsed: " + df.format(time));
    }
}
