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

package cz.vsb.genetics.sv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.util.*;

public class MultipleSvComparator {
    private FileWriter fileWriter;
    private boolean onlyCommonGenes = false;
    private Long distanceVarianceThreshold = null;
    private Double minimalProportion = null;
    private Set<StructuralVariantType> svTypes;
    private SvResultParser svParserMain;
    private List<SvResultParser> svParserOthers;
    private boolean mainPrinted;
    private int[] distanceVarianceBasesCounts;
    private boolean calculateDistanceVarianceStats = false;
    private Map<String, StructuralVariantStatsItem> distanceVarianceStats = new LinkedHashMap<>();

    public void compareStructuralVariants(SvResultParser svParserMain, List<SvResultParser> svParserOthers, String outputFile) throws Exception {
        fileWriter = new FileWriter(outputFile);
        this.svParserMain = svParserMain;
        this.svParserOthers = svParserOthers;

        printHeader(svParserMain, svParserOthers);

        mainPrinted = false;

        if (calculateDistanceVarianceStats)
            initDistanceVarianceStats();

        processStructuralVariants(StructuralVariantType.BND);
        processStructuralVariants(StructuralVariantType.INV);
        processStructuralVariants(StructuralVariantType.DUP);
        processStructuralVariants(StructuralVariantType.DEL);
        processStructuralVariants(StructuralVariantType.INS);
        processStructuralVariants(StructuralVariantType.UNK);

        fileWriter.close();
    }

    private void initDistanceVarianceStats() {
        for (SvResultParser svResultParser : svParserOthers) {
            String name = svResultParser.getName();
            
            distanceVarianceStats.put(name + StructuralVariantType.BND, new StructuralVariantStatsItem(name, StructuralVariantType.BND, svParserMain.getTranslocations().size()));
            distanceVarianceStats.put(name + StructuralVariantType.INV, new StructuralVariantStatsItem(name, StructuralVariantType.INV, svParserMain.getInversions().size()));
            distanceVarianceStats.put(name + StructuralVariantType.DUP, new StructuralVariantStatsItem(name, StructuralVariantType.DUP, svParserMain.getDuplications().size()));
            distanceVarianceStats.put(name + StructuralVariantType.DEL, new StructuralVariantStatsItem(name, StructuralVariantType.DEL, svParserMain.getDeletions().size()));
            distanceVarianceStats.put(name + StructuralVariantType.INS, new StructuralVariantStatsItem(name, StructuralVariantType.INS, svParserMain.getInsertions().size()));
            distanceVarianceStats.put(name + StructuralVariantType.UNK, new StructuralVariantStatsItem(name, StructuralVariantType.UNK, svParserMain.getUnknown().size()));
        }
    }

    public void saveDistanceVarianceStatistics(String outputFile) throws Exception {
        if (distanceVarianceStats.size() == 0)
            return;

        int columns = 2 + distanceVarianceBasesCounts.length * 2 + 1;
        String[] headers = new String[columns];
        headers[0] = "name";
        headers[1] = "sv_type";
        headers[columns - 1] = "sv_count_total";
        for (int i = 0, j = 2; i < distanceVarianceBasesCounts.length; i++, j += 2) {
            headers[j] = "dist_var_" + distanceVarianceBasesCounts[i];
            headers[j + 1] = "dist_var_pct_" + distanceVarianceBasesCounts[i];
        }

        try (FileWriter writer = new FileWriter(outputFile); CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';').withHeader(headers))) {
            for (StructuralVariantStatsItem item : distanceVarianceStats.values()) {
                String[] record = new String[columns];
                record[0] = item.getName();
                record[1] = item.getSvType().toString();
                for (int i = 0, j = 2; i < distanceVarianceBasesCounts.length; i++, j += 2) {
                    record[j] = String.valueOf(item.getSvCounts(distanceVarianceBasesCounts[i]));
                    record[j + 1] = item.getSvCountTotal() == 0 ? "0" :String.valueOf((double)(item.getSvCounts(distanceVarianceBasesCounts[i])) / (double)item.getSvCountTotal() * 100.0);
                }
                record[columns - 1] = String.valueOf(item.getSvCountTotal());

                printer.printRecord(record);
            }
        }
    }

    public boolean isOnlyCommonGenes() {
        return onlyCommonGenes;
    }

    public void setOnlyCommonGenes(boolean onlyCommonGenes) {
        this.onlyCommonGenes = onlyCommonGenes;
    }

    public Long getDistanceVarianceThreshold() {
        return distanceVarianceThreshold;
    }

    public void setDistanceVarianceThreshold(Long distanceVarianceThreshold) {
        this.distanceVarianceThreshold = distanceVarianceThreshold;
    }

    public Set<StructuralVariantType> getSvTypes() {
        return svTypes;
    }

    public void setVariantType(Set<StructuralVariantType> svTypes) {
        this.svTypes = svTypes;
    }

    public Double getMinimalProportion() {
        return minimalProportion;
    }

    public void setMinimalProportion(Double minimalProportion) {
        this.minimalProportion = minimalProportion;
    }

    private void processStructuralVariants(StructuralVariantType svType) throws Exception {
        Set<StructuralVariant> mainVariants = null;
        List<Set<StructuralVariant>> otherVariants = new ArrayList<>();
        for (SvResultParser other : svParserOthers) {
            switch (svType) {
                case BND:
                    mainVariants = svParserMain.getTranslocations();
                    otherVariants.add(other.getTranslocations());
                    break;
                case INV:
                    mainVariants = svParserMain.getInversions();
                    otherVariants.add(other.getInversions());
                    break;
                case DUP:
                    mainVariants = svParserMain.getDuplications();
                    otherVariants.add(other.getDuplications());
                    break;
                case DEL:
                    mainVariants = svParserMain.getDeletions();
                    otherVariants.add(other.getDeletions());
                    break;
                case INS:
                    mainVariants = svParserMain.getInsertions();
                    otherVariants.add(other.getInsertions());
                    break;
                case UNK:
                    mainVariants = svParserMain.getUnknown();
                    otherVariants.add(other.getUnknown());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported structural variant type");
            }
        }

        processStructuralVariants(mainVariants, otherVariants, svType);
    }

    private void processStructuralVariants(Set<StructuralVariant> mainVariants, List<Set<StructuralVariant>> otherParsersVariants,
                                           StructuralVariantType svType) throws Exception {
        Set<StructuralVariant> processedVariants = new HashSet<>();

        if (svTypes != null && !svTypes.contains(svType))
            return;

        int[] similarVariantCounts = new int[otherParsersVariants.size()];

        for (StructuralVariant structuralVariant : mainVariants) {
            if (!processedVariants.contains(structuralVariant))
                processedVariants.add(structuralVariant);
            else
                continue;

            int similarVariantsTotal = 0;
            for (int i = 0; i < otherParsersVariants.size(); i++) {
                Set<StructuralVariant> others = otherParsersVariants.get(i);

                List<StructuralVariant> similarVariants = findNearestStructuralVariants(structuralVariant, others);
                similarVariantsTotal += similarVariants.size();

                if (similarVariants.size() == 0)
                    continue;

                printSimilarVariants(structuralVariant, similarVariants, svType);
                similarVariantCounts[i]++;

                if (isCalculateDistanceVarianceStats()) {
                    addDistanceVarianceStatistics(svParserOthers.get(i).getName(), structuralVariant.getVariantType(),
                            getDistanceVariance(structuralVariant, similarVariants.get(0)));
                }
            }

            mainPrinted = false;

            if (similarVariantsTotal != 0)
                fileWriter.write("\n");
        }

        printSimpleVariantsStatistics(mainVariants, otherParsersVariants, svType, similarVariantCounts);
    }

    private List<StructuralVariant> findNearestStructuralVariants(StructuralVariant structuralVariant,
                                                                  Set<StructuralVariant> structuralVariants) {
        Map<Long, StructuralVariant> similarStructuralVariants = new TreeMap<>();

        for (StructuralVariant otherVariant : structuralVariants) {
            if (!(structuralVariant.getSrcChromosome() == otherVariant.getSrcChromosome() &&
                    structuralVariant.getDstChromosome() == otherVariant.getDstChromosome()))
                continue;

            Long distanceVariance = getDistanceVariance(structuralVariant, otherVariant);

            if (onlyCommonGenes) {
                List<String> commonGenes = getCommonGenes(structuralVariant, otherVariant);
                if (commonGenes.size() < 1)
                    continue;
            }

            if (distanceVarianceThreshold != null && distanceVariance > distanceVarianceThreshold)
                continue;

            Double proportion = getSizeProportion(structuralVariant, otherVariant);
            if (minimalProportion != null && proportion != null && proportion < minimalProportion)
                continue;

            similarStructuralVariants.put(distanceVariance, otherVariant);
        }

        return new ArrayList<>(similarStructuralVariants.values());
    }

    private Long getDistanceVariance(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        Long srcDist = Math.abs(structuralVariant.getSrcLoc() - otherVariant.getSrcLoc());
        Long dstDist = Math.abs(structuralVariant.getDstLoc() - otherVariant.getDstLoc());
        Long distanceVariance = srcDist + dstDist;
        return distanceVariance;
    }

    private void printSimpleVariantsStatistics(Set<StructuralVariant> mainVariants, List<Set<StructuralVariant>> otherParsersVariants, StructuralVariantType svType, int[] similarVariantCounts) {
        for (int i = 0; i < otherParsersVariants.size(); i++) {
            double percentage = mainVariants.size() == 0 ? 0.0 :
                    (double) similarVariantCounts[i] / (double) mainVariants.size() * 100.0;

            System.out.printf("Common SV (%s with %s / %s) - %s:\t%d/%d (%.02f%%)%n",
                    svParserMain.getName(),
                    svParserOthers.get(i).getName(),
                    svParserMain.getName(),
                    svType.toString(),
                    similarVariantCounts[i],
                    mainVariants.size(),
                    percentage);
        }
    }

    private void addDistanceVarianceStatistics(String name, StructuralVariantType svType, Long distanceVariance) throws Exception {
        String itemId = name + svType;
        StructuralVariantStatsItem item = distanceVarianceStats.get(itemId);

        if (item == null) {
            throw new IllegalAccessException("Structural variant statistics item not initialized for id: " + itemId);
        }

        for (int distanceVarianceThreshold : distanceVarianceBasesCounts) {
            if (distanceVariance <= distanceVarianceThreshold)
                item.addStructuralVariant(distanceVarianceThreshold);
        }
    }

    private void printHeader(SvResultParser svParserMain, List<SvResultParser> svParserOthers) throws Exception {
        String svLabelMain = svParserMain.getName();

        String header =
                "sv_type\t" +
                "src_chr\t" +
                "dst_chr\t" +
                svLabelMain + "_src_pos\t" +
                svLabelMain + "_dst_pos\t" +
                svLabelMain + "_sv_size\t" +
                svLabelMain + "_sv_fraction\t" +
                svLabelMain + "_gene\t" +
                svLabelMain + "_id";

        for (SvResultParser svParserOther : svParserOthers) {
            String svLabelOther = svParserOther.getName();

            header += "\t" +
                svLabelOther + "_src_pos\t" +
                svLabelOther + "_dst_pos\t" +
                svLabelOther + "_sv_size\t" +
                svLabelOther + "_sv_fraction\t" +
                svLabelOther + "_src_pos_dist\t" +
                svLabelOther + "_dst_pos_dist\t" +
                svLabelOther + "_dist_var\t" +
                svLabelOther + "_gene\t" +
                svLabelOther + "_common_genes\t" +
                svLabelOther + "_size_difference\t" +
                svLabelOther + "_size_proportion\t" +
                svLabelOther + "_id";
        }

        fileWriter.write(header + "\n");
    }

    private void printSimilarVariants(StructuralVariant variant,
                                      List<StructuralVariant> similarVariants, StructuralVariantType svType) throws Exception {
        StructuralVariant similarStructuralVariant = similarVariants.get(0);

        Long srcDist = Math.abs(variant.getSrcLoc() - similarStructuralVariant.getSrcLoc());
        Long dstDist = Math.abs(variant.getDstLoc() - similarStructuralVariant.getDstLoc());

        String commonGenes = StringUtils.join(getCommonGenes(variant, similarStructuralVariant), ",");

        String line = "";

        if (!mainPrinted) {
            line +=
                    svType.toString() + "\t" +
                    variant.getSrcChromosome().toString() + "\t" +
                    variant.getDstChromosome().toString() + "\t" +
                    variant.getSrcLoc() + "\t" +
                    variant.getDstLoc() + "\t" +
                    variant.getSize() + "\t" +
                    getAllelicFraction(variant) + "\t" +
                    variant.getGene() + "\t" +
                    getVariantId(variant);

            mainPrinted = true;
        }

        line += "\t" +
                similarStructuralVariant.getSrcLoc() + "\t" +
                similarStructuralVariant.getDstLoc() + "\t" +
                similarStructuralVariant.getSize() + "\t" +
                getAllelicFraction(similarStructuralVariant) + "\t" +
                srcDist + "\t" +
                dstDist + "\t" +
                (srcDist + dstDist) + "\t" +
                similarStructuralVariant.getGene() + "\t" +
                commonGenes + "\t" +
                getSizeDifference(variant, similarStructuralVariant) + "\t" +
                getSizeProportionAsString(variant, similarStructuralVariant) + "\t" +
                getVariantId(similarStructuralVariant);

        fileWriter.write(line);
    }

    private List<String> getCommonGenes(StructuralVariant sv1, StructuralVariant sv2) {
        if (StringUtils.isBlank(sv1.getGene()) || StringUtils.isBlank(sv2.getGene()))
            return Collections.emptyList();

        String[] sv1Genes = sv1.getGene().toUpperCase().split("[/;]");
        List<String> sv2Genes = Arrays.asList(sv2.getGene().toUpperCase().split("[/;]"));

        List<String> commonGenes = new ArrayList<>();

        for (String gene : sv1Genes) {
            if (sv2Genes.contains(gene))
                commonGenes.add(gene);
        }

        return commonGenes;
    }

    private String getAllelicFraction(StructuralVariant structuralVariant) {
        if (structuralVariant.getVariantAllelicFraction() == null)
            return "";

        return String.format("%.02f", structuralVariant.getVariantAllelicFraction());
    }

    private String getSizeDifference(StructuralVariant main, StructuralVariant other) {
        if (main.getSize() == 0 || other.getSize() == 0)
            return "";

        return Long.toString(other.getSize() - main.getSize());
    }

    private String getSizeProportionAsString(StructuralVariant main, StructuralVariant other) {
        Double proportion = getSizeProportion(main, other);

        return proportion == null ? "" : String.format("%.02f", proportion).replaceAll("-","");
    }

    private Double getSizeProportion(StructuralVariant main, StructuralVariant other) {
        if (main.getSize() == 0 || other.getSize() == 0)
            return null;

        return (double)other.getSize() / (double)main.getSize();
    }

    private String getVariantId(StructuralVariant structuralVariant) {
        return structuralVariant.getId() == null ? "" : structuralVariant.getId();
    }

    public void setDistanceVarianceBasesCounts(int[] distanceVarianceBasesCounts) {
        this.distanceVarianceBasesCounts = distanceVarianceBasesCounts;
    }

    public void setCalculateDistanceVarianceStats(boolean calculateDistanceVarianceStats) {
        this.calculateDistanceVarianceStats = calculateDistanceVarianceStats;
    }

    public boolean isCalculateDistanceVarianceStats() {
        return calculateDistanceVarianceStats && distanceVarianceBasesCounts != null && distanceVarianceBasesCounts.length != 0;
    }
}
