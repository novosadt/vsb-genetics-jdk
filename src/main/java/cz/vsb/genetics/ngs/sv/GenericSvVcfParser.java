package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParserBase;
import cz.vsb.genetics.util.GeneAnnotator;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericSvVcfParser extends SvResultParserBase {

    private String[] header;
    private final Pattern chromLocPatternWithChr = Pattern.compile("(chr\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private final Pattern chromLocPatternWithoutChr = Pattern.compile("(\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private boolean preferBaseSvType;
    private boolean onlyFilterPass;
    private int filteredCount = 0;
    private int totalCount = 0;
    private int skippedCount = 0;

    public GenericSvVcfParser(String name) {
        this.name = name;
    }

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        reset();

        filteredCount = totalCount = skippedCount = 0;

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
        printStructuralVariantStats(name);
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
        totalCount++;

        String srcChromId = values.get("CHROM");
        int srcLoc = Integer.valueOf(values.get("POS"));
        String id = values.get("ID");
        String filter = values.get("FILTER");
        String dstChromId = srcChromId;
        Map<String, String> info = getInfo(values.get("INFO"));
        int dstLoc = info.containsKey("END") ? Integer.valueOf(info.get("END")) : 0;
        String svType = info.get("SVTYPE").toLowerCase();
        int svLength = getSvLength(info);


        if (onlyFilterPass && StringUtils.isNotBlank(filter) && !filter.trim().equalsIgnoreCase("pass")) {
            System.err.printf("Filtering variant: %s - filter: %s\n", id, filter);
            filteredCount++;
            return;
        }

        if (svType.equals("bnd")) {
            String chromLoc = values.get("ALT").toLowerCase();
            Matcher m = chromLocPatternWithChr.matcher(chromLoc);
            if (!m.find()) {
                m = chromLocPatternWithoutChr.matcher(chromLoc);
                if (!m.find()) {
                    System.err.printf("Skipping BND variant: %s - unsupported destination chromosome location format: %s\n", id, chromLoc);
                    skippedCount++;
                    return;
                }
            }

            dstChromId = m.group(1);
            dstLoc = Integer.valueOf(m.group(2));
            svType = getBndVariantType(info).toString().toLowerCase();

            // There are paired entries for Inversions and Duplications
            // in VCF with BND structural variant type. If a variant is of type Inversion or Duplication
            // on the same chromosome, take only one of them - skip entry with start breakpoint location
            // greater than end breakpoint location
            if ((svType.equals("inv") || svType.equals("dup")) && srcLoc > dstLoc) {
                totalCount--;
                return;
            }
        }

        if (svType.equals("ins"))
            dstLoc = srcLoc + svLength;

        //Structural variant type determined from SVTYPE2 differs from BND, but there is no information SVLEN,
        //so we have to calculate it by hand
        if (!svType.equals("bnd") && svLength == 0)
            svLength = dstLoc - srcLoc;

        Chromosome srcChrom = Chromosome.of(srcChromId);
        Chromosome dstChrom = Chromosome.of(dstChromId);

        if (srcChrom == null || dstChrom == null) {
            System.err.printf("Skipping variant id: %s - unsupported chromosome format. Source chromosome: %s, Destination chromosome: %s\n", id, srcChromId, dstChromId);
            skippedCount++;
            return;
        }

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, svLength);
        sv.setId(id);
        sv.setInfo(info);
        sv.setVariantAlleleFraction(getAllelicFraction(info));

        Set<StructuralVariant> variants;
        StructuralVariantType type;
        
        switch (svType) {
            case "bnd" : variants = translocations; type = StructuralVariantType.BND; break;
            case "cnv" : variants = copyNumberVariations; type = StructuralVariantType.CNV; break;
            case "del" : variants = deletions; type = StructuralVariantType.DEL; break;
            case "ins" : variants = insertions; type = StructuralVariantType.INS; break;
            case "dup" : variants = duplications; type = StructuralVariantType.DUP; break;
            case "inv" : variants = inversions; type = StructuralVariantType.INV; break;
            default: variants = unknown; type = StructuralVariantType.UNK;
        }

        if (geneAnnotator != null)
            sv.setGenes(GeneAnnotator.toSymbols(geneAnnotator.findIntersectingGenes(sv)));

        if (!addStructuralVariant(sv, variants, type))
            totalCount--;
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

    private int getSvLength(Map<String, String> info) {
        if (StringUtils.isNotBlank(info.get("SVLEN"))) {
            try {
                return Math.abs(Integer.valueOf(info.get("SVLEN")));
            } catch (NumberFormatException e) {
                // structural variant size cannot be obtained from INFO - NaN
            }
        }

        return 0;
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

    @Override
    protected void printStructuralVariantStats(String parserName) {
        super.printStructuralVariantStats(parserName);

        System.out.printf("Filtered SV count:\t\t%d (%.2f%%)\n", filteredCount, (double) filteredCount / (double) totalCount * 100.0);
        System.out.printf("Skipped SV count:\t\t%d (%.2f%%)\n", skippedCount, (double) skippedCount / (double) totalCount * 100.0);
        System.out.printf("Total SV count:\t\t\t%d\n", totalCount);
    }

    public void setPreferBaseSvType(boolean preferBaseSvType) {
        this.preferBaseSvType = preferBaseSvType;
    }

    public void setOnlyFilterPass(boolean onlyFilterPass) {
        this.onlyFilterPass = onlyFilterPass;
    }
}
