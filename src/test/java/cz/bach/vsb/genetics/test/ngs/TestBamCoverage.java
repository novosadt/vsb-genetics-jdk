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
import cz.vsb.genetics.ngs.BamCoverageInfo;

import java.util.List;

public class TestBamCoverage {
    public static void main(String[] args) {
        try {
            //testBamCoverageAtPosition();
            testBamCoverageAtInterval();
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void testBamCoverageAtPosition() throws Exception {
        String bamFile = "./data/ngs/bam/test.bam";
        String indexFile = "./data/ngs/bam/test.bai";

        Chromosome chromosome = Chromosome.chr17;
        int position = 7675949;

        BamCoverageInfo bamCoverageInfo = new BamCoverageInfo(bamFile, indexFile);
        long coverage = bamCoverageInfo.getCoverage(chromosome, position);

        String info = String.format("Coverage at position %s:%d - %d", chromosome.toString(), position, coverage);

        System.out.println(info);

        bamCoverageInfo.close();
    }

    public static void testBamCoverageAtInterval() throws Exception {
        String bamFile = "./data/ngs/bam/test.bam";
        String indexFile = "./data/ngs/bam/test.bai";

        Chromosome chromosome = Chromosome.chr17;
        int start = 7675923;
        int end = 7675999;

        BamCoverageInfo bamCoverageInfo = new BamCoverageInfo(bamFile, indexFile);
        List<Long> coverages = bamCoverageInfo.getCoverage(chromosome, start, end);

        for (Long coverage : coverages)
            System.out.println(String.format("Coverage at position %s:%d - %d", chromosome.toString(), start++, coverage));

        bamCoverageInfo.close();
    }
}
