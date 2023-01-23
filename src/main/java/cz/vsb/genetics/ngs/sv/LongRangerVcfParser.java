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

public class LongRangerVcfParser extends SvResultParserBase {
    private String[] header;
    private final Pattern chromLocPatternWithChr = Pattern.compile("(chr\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private final Pattern chromLocPatternWithoutChr = Pattern.compile("(\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private final boolean preferBaseSvType;

    public LongRangerVcfParser(boolean preferBaseSvType) {
        this.preferBaseSvType = preferBaseSvType;
    }

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while((line = reader.readLine()) != null) {
            if (StringUtils.isBlank(line))
                continue;

            if (line.startsWith("##"))
                continue;

            if (line.startsWith("#")) {
                line = line.replaceFirst("#", "");
                header = line.split(delim);
                continue;
            }

            parseLine(line, delim);
        }

        reader.close();
    }

    @Override
    public void printStructuralVariantStats() {
        printStructuralVariantStats("VCF-LongRanger");
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
        String id = values.get("ID");
        String srcChromId = values.get("CHROM");
        String dstChromId = srcChromId;
        Map<String, String> info = getInfo(values.get("INFO"));
        Long srcLoc = new Long(values.get("POS"));
        Long dstLoc = info.containsKey("END") ? new Long(info.get("END")) : 0;
        Long svLength = StringUtils.isBlank(info.get("SVLEN")) ? 0 : Math.abs(new Long(info.get("SVLEN")));
        String svType = info.get("SVTYPE").toLowerCase();

        if (svType.equals("bnd")) {
            // There are paired entries in VCF with BND structural variant type - for now skip them
            if (srcLoc > dstLoc)
                return;

            String chromLoc = values.get("ALT");
            Matcher m = chromLocPatternWithChr.matcher(chromLoc);
            if (!m.find()) {
                m = chromLocPatternWithoutChr.matcher(chromLoc);
                if (!m.find()) {
                    System.err.printf("Skipping BND variant (%s) - unsupported destination chromosome location format: %s\n", id, chromLoc);
                    return;
                }
            }

            dstChromId = m.group(1);
            dstLoc = new Long(m.group(2));
            svType = getBndVariantType(info).toString().toLowerCase();
        }

        if (svType.equals("ins"))
            dstLoc = srcLoc + svLength;

        //Structural variant type determined from SVTYPE2 differs from BND, but there is no information SVLEN,
        //so we have to calculate it by hand
        if (!svType.equals("bnd") && svLength == 0)
            svLength = dstLoc - srcLoc;

        Chromosome srcChrom = Chromosome.getChromosome(srcChromId);
        Chromosome dstChrom = Chromosome.getChromosome(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, svLength);
        sv.setId(id);
        sv.setInfo(info);
        sv.setVariantAlleleFraction(getAllelicFraction(info));

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

    private Double getAllelicFraction(Map<String, String> info) {
        String allelicFrac = info.get("ALLELIC_FRAC");

        if (allelicFrac == null)
            return null;

        try {
            return new Double(allelicFrac);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

}
