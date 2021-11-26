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


package cz.vsb.genetics.sv.om;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.common.StructuralVariant;
import cz.vsb.genetics.common.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParserBase;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

public class BionanoPipelineResultParser extends SvResultParserBase {

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while((line = reader.readLine()) != null) {
            if (line.startsWith("#") || StringUtils.isBlank(line))
                continue;

            parseLine(line, delim);
        }

        reader.close();
    }

    @Override
    public void printStructuralVariantStats() {
        printStructuralVariantStats("Bionano Genomics");
    }

    private void parseLine(String line, String delim) {
        String[] values = line.split(delim);

        addStructuralVariant(values);
    }

    private void addStructuralVariant(String[] values) {
        String srcChromId = values[2];
        String dstChromId = values[3];
        Long srcLoc = new Double(values[6]).longValue();
        Long dstLoc = new Double(values[7]).longValue();
        String type = values[9].toLowerCase();
        long size = dstLoc - srcLoc;
        String gene = values.length < 35 ? "" : values[34];

        if (type.contains("translocation"))
            size = 0L;

        Chromosome srcChrom = Chromosome.getChromosome(srcChromId);
        Chromosome dstChrom = Chromosome.getChromosome(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, size, gene);

        if (type.contains("translocation"))
            addStructuralVariant(sv, translocations, StructuralVariantType.BND);
        else if (type.contains("deletion"))
            addStructuralVariant(sv, deletions, StructuralVariantType.DEL);
        else if (type.contains("insertion"))
            addStructuralVariant(sv, insertions, StructuralVariantType.INS);
        else if (type.contains("inversion"))
            addStructuralVariant(sv, inversions, StructuralVariantType.INV);
        else if (type.contains("duplication"))
            addStructuralVariant(sv, duplications, StructuralVariantType.DUP);
        else
            addStructuralVariant(sv, unknown, StructuralVariantType.UNK);
    }
}
