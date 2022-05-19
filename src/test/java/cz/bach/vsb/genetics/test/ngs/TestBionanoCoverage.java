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
import cz.vsb.genetics.om.coverage.BionanoCoverageInfo;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

public class TestBionanoCoverage {
    public static void main(String args[]) {
        try {
            Chromosome chromosome = Chromosome.chr1;
            int start = 12935517;
            int end = 13935517;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            testOmCoverageAtPosition(chromosome, start);
            testOmCoverageAtInterval(chromosome, start, end);

            stopWatch.stop();
            System.out.println("Time (mils): " + stopWatch.getTime());
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void testOmCoverageAtPosition(Chromosome chromosome, int position) throws Exception {
        String refCMap = "./data/om/test_ref.cmap";
        String qryCMap = "./data/om/test_query.cmap";
        String xmap = "./data/om/test.xmap";

        BionanoCoverageInfo coverageInfo = new BionanoCoverageInfo(refCMap, qryCMap, xmap);
        coverageInfo.open();
        long coverage = coverageInfo.getCoverage(chromosome, position);

        String info = String.format("Coverage at label closest to position %s:%d - %d", chromosome, position, coverage);

        System.out.println(info);

        coverageInfo.close();
    }

    public static void testOmCoverageAtInterval(Chromosome chromosome, int start, int end) throws Exception {
        String refCMap = "./data/om/test_ref.cmap";
        String qryCMap = "./data/om/test_query.cmap";
        String xmap = "./data/om/test.xmap";

        BionanoCoverageInfo coverageInfo = new BionanoCoverageInfo(refCMap, qryCMap, xmap);
        coverageInfo.open();
        List<Long> coverages = coverageInfo.getCoverage(chromosome, start, end);

        System.out.printf("Coverages at labels at interval %s:%d - %d\n", chromosome, start, end);
        for (Long coverage : coverages)
            System.out.printf("Coverage: - %d\n", coverage);

        coverageInfo.close();
    }
}
