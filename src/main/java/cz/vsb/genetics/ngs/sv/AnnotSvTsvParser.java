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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotSvTsvParser extends SvResultParserBase {
    private final Pattern chromLocPatternWithChr = Pattern.compile("(chr\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private final Pattern chromLocPatternWithoutChr = Pattern.compile("(\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private String[] header;
    private final boolean preferBaseSvType;

    public AnnotSvTsvParser(boolean preferBaseSvType) {
        this.preferBaseSvType = preferBaseSvType;
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
        printStructuralVariantStats("AnnotSV");
    }

    private void parseLine(String line, String delim) throws Exception {
        String[] tmp = line.split(delim);

        assert tmp.length == header.length;

        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < tmp.length; i++)
            values.put(header[i], tmp[i]);

        addStructuralVariant(values);
    }

    private void addStructuralVariant(Map<String, String> values) throws Exception {
        String srcChromId = "chr" + values.get("SV_chrom");
        String dstChromId = srcChromId;
        Long srcLoc = new Long(values.get("SV_start"));
        Long dstLoc = new Long(values.get("SV_end"));
        Long svLength = StringUtils.isBlank(values.get("SV_length")) ? 0 : Math.abs(new Long(values.get("SV_length")));
        String svType = values.get("SV_type").toLowerCase();
        Map<String, String> info = getInfo(values.get("INFO"));

        // To preserve backward compatibility. Column label differs between versions of AnnotSV
        String annotSvType = values.get("AnnotSV_type") == null ? null : values.get("AnnotSV_type").toLowerCase();
        if (annotSvType == null)
            annotSvType = values.get("Annotation_mode") == null ? null : values.get("Annotation_mode").toLowerCase();

        String gene = values.get("Gene_name");

        if (!"full".equals(annotSvType))
            return;

        if (svType.equals("bnd")) {
            StructuralVariantType type = getBndVariantType(info);
            if (type == StructuralVariantType.BND) {
                String chromLoc = values.get("ALT");
                Matcher m = chromLocPatternWithChr.matcher(chromLoc);
                if (!m.find()) {
                    m = chromLocPatternWithoutChr.matcher(chromLoc);
                    if (!m.find())
                        throw new Exception("Unsupported chromosome:location format: " + chromLoc);
                }

                dstChromId = m.group(1);
                dstLoc = new Long(m.group(2));
            }
            else {
                svType = type.toString().toLowerCase();
            }
        }

        if (svType.equals("ins"))
            dstLoc = srcLoc + svLength;

        Chromosome srcChrom = Chromosome.getChromosome(srcChromId);
        Chromosome dstChrom = Chromosome.getChromosome(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, svLength, gene);
        sv.setId(values.get("ID"));
        sv.setInfo(info);

        switch (svType) {
            case "bnd" : addStructuralVariant(sv, translocations, StructuralVariantType.BND); break;
            case "cnv" : addStructuralVariant(sv, copyNumberVariations, StructuralVariantType.CNV); break;
            case "del" : addStructuralVariant(sv, deletions, StructuralVariantType.DEL); break;
            case "ins" : addStructuralVariant(sv, insertions, StructuralVariantType.INS); break;
            case "dup" : addStructuralVariant(sv, duplications, StructuralVariantType.DUP); break;
            case "inv" : addStructuralVariant(sv, inversions, StructuralVariantType.INV); break;
            default: addStructuralVariant(sv, unknown, StructuralVariantType.UNK);
        }
    }

    private Map<String, String> getInfo(String info) {
        String[] infoValues = info.split(";");

        Map<String, String> values = new HashMap<>();

        for (String infoValue : infoValues) {
            String[] keyValue = infoValue.split("=");
            values.put(keyValue[0], keyValue.length == 1 ? null : keyValue[1]);
        }

        return values;
    }

    private StructuralVariantType getBndVariantType(Map<String, String> info) {
        if (preferBaseSvType)
            return StructuralVariantType.BND;

        String svType2 = info.get("SVTYPE2");

        if (StringUtils.isBlank(svType2))
            return StructuralVariantType.BND;

        return StructuralVariantType.valueOf(svType2.toUpperCase());
    }
}
