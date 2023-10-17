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
import cz.vsb.genetics.om.coverage.BionanoCoverageCalculator;
import org.apache.commons.lang3.time.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TestBionanoCoverage {
    private static final String CMAP_REF = "./src/test/resources/coverage/bionano/test_ref.cmap";
    private static final String CMAP_QRY = "./src/test/resources/coverage/bionano/test_query.cmap";
    private static final String XMAP = "./src/test/resources/coverage/bionano/test.xmap";

    public static void main(String[] args) {
        try {
            Chromosome chromosome = Chromosome.chr1;
            int start = 12935517;
            int end = 13935517;

            testOmCoverageAtPosition(chromosome, start);
            testOmCoverageAtInterval(chromosome, start, end);
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void testOmCoverageAtPosition(Chromosome chromosome, int position) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        BionanoCoverageCalculator coverageInfo = new BionanoCoverageCalculator(CMAP_REF, CMAP_QRY, XMAP);
        coverageInfo.open();
        int coverage = coverageInfo.getPositionCoverage(chromosome, position);
        coverageInfo.close();

        stopWatch.stop();
        printTime(stopWatch.getTime());

        System.out.printf("Coverage at label closest to position %s:%d - %d\n", chromosome, position, coverage);
    }

    public static void testOmCoverageAtInterval(Chromosome chromosome, int start, int end) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        BionanoCoverageCalculator coverageInfo = new BionanoCoverageCalculator(CMAP_REF, CMAP_QRY, XMAP);
        coverageInfo.open();
        int[] coverages = coverageInfo.getIntervalCoverage(chromosome, start, end).getCoverages();
        coverageInfo.close();

        stopWatch.stop();
        printTime(stopWatch.getTime());

        System.out.printf("Coverages at labels at interval %s:%d - %d\n", chromosome, start, end);
        for (int coverage : coverages)
            System.out.printf("Coverage: - %d\n", coverage);
    }

    private static void printTime(long mils) {
        Date time = new Date(mils);

        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("Time elapsed: " + df.format(time));
    }
}
