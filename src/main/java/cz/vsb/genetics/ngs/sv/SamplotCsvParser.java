/*
 * Copyright (C) 2022  Tomas Novosad
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


package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParserBase;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SamplotCsvParser extends SvResultParserBase {
    private String[] header;

    public SamplotCsvParser(String name) {
        this.name = name;
    }

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        header = reader.readLine().split(delim);

        String line;
        while((line = reader.readLine()) != null) {
            if (StringUtils.isBlank(line))
                continue;

            parseLine(line, delim);
        }

        reader.close();
    }

    @Override
    public void printStructuralVariantStats() {
        printStructuralVariantStats("Samplot");
    }

    private void parseLine(String line, String delim) {
        String[] tmp = line.split(delim);

        assert tmp.length == header.length;

        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < tmp.length; i++)
            values.put(header[i], tmp[i]);

        addStructuralVariant(values);
    }

    private void addStructuralVariant(Map<String, String> values) {
        String srcChromId = values.get("contig_start");
        String dstChromId = values.get("contig_end");
        Long srcLoc = new Long(values.get("sv_start"));
        Long dstLoc = new Long(values.get("sv_end"));
        Long svLength = StringUtils.isBlank(values.get("sv_size")) ? 0 : Math.abs(new Long(values.get("sv_size")));
        String svType = values.get("sv_type");

        Chromosome srcChrom = Chromosome.of(srcChromId);
        Chromosome dstChrom = Chromosome.of(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, svLength, null);

        switch (svType) {
            case "InterChrm" :
            case "InterChrmInversion" :
                addStructuralVariant(sv, translocations, StructuralVariantType.BND); break;
            case "Deletion/Normal" : addStructuralVariant(sv, deletions, StructuralVariantType.DEL); break;
            case "Deletion" : addStructuralVariant(sv, deletions, StructuralVariantType.DEL); break;
            case "Duplication" : addStructuralVariant(sv, duplications, StructuralVariantType.DUP); break;
            case "Inversion" : addStructuralVariant(sv, inversions, StructuralVariantType.INV); break;
            default: addStructuralVariant(sv, unknown, StructuralVariantType.UNK);
        }
    }
}
