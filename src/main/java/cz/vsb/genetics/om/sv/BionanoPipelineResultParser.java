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


package cz.vsb.genetics.om.sv;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.om.struct.bionano.SMap;
import cz.vsb.genetics.om.struct.bionano.SMapEntry;
import cz.vsb.genetics.om.struct.bionano.SMapParser;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParserBase;

import java.util.HashMap;
import java.util.Map;

public class BionanoPipelineResultParser extends SvResultParserBase {
    private Map<String, StructuralVariant> inversionBreakpoints = new HashMap<>();
    private Map<String, String> inversionBreakpointLinks = new HashMap<>();
    private Map<String, String> inversionBreakpointTypes = new HashMap<>();

    public BionanoPipelineResultParser(String name) {
        this.name = name;
    }

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        SMapParser parser = new SMapParser();
        SMap smap = parser.parse(file);

        for (SMapEntry entry : smap.getEntries())
            addStructuralVariant(entry);

        processInversions();
    }

    @Override
    public void printStructuralVariantStats() {
        printStructuralVariantStats("Bionano Genomics");
    }

    private void addStructuralVariant(SMapEntry entry) {
        String srcChromId = entry.getRefcontigID1().toString();
        String dstChromId = entry.getRefcontigID2().toString();
        Long srcLoc = entry.getRefStartPos().longValue();
        Long dstLoc = entry.getRefEndPos().longValue();
        String type = entry.getType().toLowerCase();
        long size = dstLoc - srcLoc;
        String gene = entry.getOverlapGenes() == null ? "" : entry.getOverlapGenes();

        if (type.contains("translocation"))
            size = 0L;

        Chromosome srcChrom = Chromosome.of(srcChromId);
        Chromosome dstChrom = Chromosome.of(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, size, gene);
        sv.setVariantAlleleFraction(entry.getVaf());
        sv.setId(entry.getSmapEntryID().toString());

        if (type.contains("translocation"))
            addStructuralVariant(sv, translocations, StructuralVariantType.BND);
        else if (type.contains("deletion"))
            addStructuralVariant(sv, deletions, StructuralVariantType.DEL);
        else if (type.contains("insertion"))
            addStructuralVariant(sv, insertions, StructuralVariantType.INS);
        else if (type.contains("inversion")) {
            inversionBreakpoints.put(sv.getId(), sv);
            inversionBreakpointLinks.put(sv.getId(), entry.getLinkID().toString());
            inversionBreakpointTypes.put(sv.getId(), type);
        }
        else if (type.contains("duplication"))
            addStructuralVariant(sv, duplications, StructuralVariantType.DUP);
        else
            addStructuralVariant(sv, unknown, StructuralVariantType.UNK);
    }

    private void processInversions() {
        for (StructuralVariant breakpoint : inversionBreakpoints.values()) {
            String type = inversionBreakpointTypes.get(breakpoint.getId());

            if (!(type.equals("inversion") || type.equals("inversion_paired")))
                continue;

            StructuralVariant link = inversionBreakpoints.get(inversionBreakpointLinks.get(breakpoint.getId()));

            long baseStart = breakpoint.getDstLoc() != -1 ? breakpoint.getDstLoc() : breakpoint.getSrcLoc();
            long linkStart = link.getSrcLoc() != -1 ? link.getSrcLoc() : link.getDstLoc();

            StructuralVariant breakpointA = breakpoint;
            StructuralVariant breakpointB = link;

            if (baseStart > linkStart) {
                breakpointA = link;
                breakpointB = breakpoint;
            }

            long startPos = breakpointA.getDstLoc() != -1 ? breakpointA.getDstLoc() : breakpointA.getSrcLoc();
            long endPos = breakpointB.getSrcLoc() != -1 ? breakpointB.getSrcLoc() : breakpointB.getDstLoc();
            long size = endPos - startPos;

            StructuralVariant inversion = new StructuralVariant(breakpoint.getSrcChromosome(), startPos, breakpoint.getDstChromosome(), endPos, size, breakpoint.getGene());
            inversion.setVariantAlleleFraction(breakpoint.getVariantAllelicFraction());
            inversion.setId(breakpoint.getId());

            addStructuralVariant(inversion, inversions, StructuralVariantType.INV);
        }
    }
}
