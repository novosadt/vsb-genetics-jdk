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

import cz.vsb.genetics.common.ChromosomeRegion;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.*;

public class MultipleSvComparator {
    private static final Logger log = LoggerFactory.getLogger(MultipleSvComparator.class);

    private FileWriter fileWriter;
    private boolean onlyCommonGenes = false;
    private Integer distanceVarianceThreshold = null;
    private Double intersectionVarianceThreshold = null;
    private Double minimalProportion = null;
    private Set<StructuralVariantType> svTypes;
    private SvResultParser svParserMain;
    private List<SvResultParser> svParserOthers;
    private boolean mainPrinted;
    private int[] distanceVarianceBasesCounts;
    private double[] intersectionVarianceThresholds;
    private boolean calculateStructuralVariantStats = false;
    private Map<String, StructuralVariantStatsItem> structuralVariantStats = new LinkedHashMap<>();
    private List<ChromosomeRegion> excludedRegions;

    public void compareStructuralVariants(SvResultParser svParserMain, List<SvResultParser> svParserOthers, String outputFile) throws Exception {
        fileWriter = new FileWriter(outputFile);
        this.svParserMain = svParserMain;
        this.svParserOthers = svParserOthers;

        printHeader(svParserMain, svParserOthers);

        mainPrinted = false;

        if (calculateStructuralVariantStats)
            initStructuralVariantStats();

        processStructuralVariants(StructuralVariantType.BND);
        processStructuralVariants(StructuralVariantType.INV);
        processStructuralVariants(StructuralVariantType.DUP);
        processStructuralVariants(StructuralVariantType.DEL);
        processStructuralVariants(StructuralVariantType.INS);
        processStructuralVariants(StructuralVariantType.UNK);

        fileWriter.close();
    }

    private void initStructuralVariantStats() {
        for (SvResultParser svResultParser : svParserOthers) {
            String name = svResultParser.getName();
            
            structuralVariantStats.put(name + StructuralVariantType.BND, new StructuralVariantStatsItem(name, StructuralVariantType.BND));
            structuralVariantStats.put(name + StructuralVariantType.INV, new StructuralVariantStatsItem(name, StructuralVariantType.INV));
            structuralVariantStats.put(name + StructuralVariantType.DUP, new StructuralVariantStatsItem(name, StructuralVariantType.DUP));
            structuralVariantStats.put(name + StructuralVariantType.DEL, new StructuralVariantStatsItem(name, StructuralVariantType.DEL));
            structuralVariantStats.put(name + StructuralVariantType.INS, new StructuralVariantStatsItem(name, StructuralVariantType.INS));
            structuralVariantStats.put(name + StructuralVariantType.UNK, new StructuralVariantStatsItem(name, StructuralVariantType.UNK));
        }
    }

    public void saveStructuralVariantStats(String outputFile) throws Exception {
        if (structuralVariantStats.size() == 0)
            return;

        List<String> headers = new ArrayList<>();
        headers.add("name");
        headers.add("sv_type");
        headers.add("sv_count_total");

        if (distanceVarianceBasesCounts != null) {
            for (int basesCount : distanceVarianceBasesCounts) {
                headers.add("dist_var_" + basesCount);
                headers.add("dist_var_pct_" + basesCount);
            }
        }

        if (intersectionVarianceThresholds != null) {
            for (double threshold:  intersectionVarianceThresholds) {
                headers.add("intersect_var_" + threshold);
                headers.add("intersect_var_pct_" + threshold);
            }
        }

        try (FileWriter writer = new FileWriter(outputFile);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader(headers.toArray(new String[0])).build())) {

            for (StructuralVariantStatsItem item : structuralVariantStats.values()) {
                List<String> record = new ArrayList<>();
                record.add(item.getName());
                record.add(item.getSvType().toString());
                record.add(String.valueOf(item.getSvCountTotal()));

                if (distanceVarianceBasesCounts != null) {
                    for (int basesCount : distanceVarianceBasesCounts) {
                        int svCount = item.getSvCountsDistanceVariance(basesCount);
                        record.add(String.valueOf(svCount));
                        record.add(String.format("%.2f", item.getSvCountTotal() == 0 ? 0.0 : (double) (svCount) / (double) item.getSvCountTotal() * 100.0));
                    }
                }

                if (intersectionVarianceThresholds != null) {
                    for (double threshold : intersectionVarianceThresholds) {
                        int svCount = item.getSvCountsIntersectionVariance(threshold);
                        record.add(String.valueOf(svCount));
                        record.add(String.format("%.2f", item.getSvCountTotal() == 0 ? 0.0 : (double) (svCount) / (double) item.getSvCountTotal() * 100.0));
                    }
                }

                printer.printRecord(record);
            }
        }
    }

    public void setOnlyCommonGenes(boolean onlyCommonGenes) {
        this.onlyCommonGenes = onlyCommonGenes;
    }

    public void setDistanceVarianceThreshold(Integer distanceVarianceThreshold) {
        this.distanceVarianceThreshold = distanceVarianceThreshold;
    }

    public void setIntersectionVarianceThreshold(Double intersectionVarianceThreshold) {
        this.intersectionVarianceThreshold = intersectionVarianceThreshold;
    }

    public void setVariantTypes(Set<StructuralVariantType> svTypes) {
        this.svTypes = svTypes;
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
        int unfilteredSvCount = 0;

        for (StructuralVariant structuralVariant : mainVariants) {
            applyVariantFilters(structuralVariant);

            if (!structuralVariant.isFiltered())
                unfilteredSvCount++;

            if (!processedVariants.contains(structuralVariant))
                processedVariants.add(structuralVariant);
            else
                continue;

             for (int i = 0; i < otherParsersVariants.size(); i++) {
                Set<StructuralVariant> others = otherParsersVariants.get(i);

                List<StructuralVariant> similarVariants = findNearestStructuralVariants(structuralVariant, others);

                printSimilarVariants(structuralVariant, similarVariants, svType);

                if (similarVariants.size() == 0)
                    continue;

                similarVariantCounts[i]++;

                if (calculateStructuralVariantStats && !structuralVariant.isFiltered()) {
                    addStructuralVariantStats(structuralVariant, i, similarVariants);
                }
            }

            mainPrinted = false;
            fileWriter.write("\n");
        }

        if (calculateStructuralVariantStats)
            setStatsSvCountTotal(unfilteredSvCount, svType);

        printSimpleVariantsStatistics(mainVariants, otherParsersVariants, svType, similarVariantCounts);
    }

    private void applyVariantFilters(StructuralVariant variant) {
        if (excludedRegions != null && excludedRegions.size() > 0) {
            for (ChromosomeRegion excluded : excludedRegions) {
                if (excluded.isInRegion(variant.getSrcChromosome(), variant.getSrcLoc()) ||
                        excluded.isInRegion(variant.getDstChromosome(), variant.getDstLoc())) {
                    log.info(String.format("FILTER: %s - Variant (%s) id: %s", StructuralVariantFilter.EXCLUDED_REGION, variant.getVariantType(), variant.getId()));
                    variant.addFilter(StructuralVariantFilter.EXCLUDED_REGION);
                    break;
                }
            }
        }
    }

    private void setStatsSvCountTotal(int svCountTotal, StructuralVariantType svType) {
        for (SvResultParser svResultParser : svParserOthers) {
            String name = svResultParser.getName();

            structuralVariantStats.get(name + svType).setSvCountTotal(svCountTotal);
        }
    }

    private void addStructuralVariantStats(StructuralVariant structuralVariant, int otherParserIndex, List<StructuralVariant> similarVariants) throws Exception {
        if (distanceVarianceBasesCounts != null) {
            addDistanceVarianceStatistics(svParserOthers.get(otherParserIndex).getName(), structuralVariant.getVariantType(),
                    getDistanceVariance(structuralVariant, similarVariants.get(0)));
        }

        if (intersectionVarianceThresholds != null) {
            addIntersectionVarianceStats(svParserOthers.get(otherParserIndex).getName(), structuralVariant.getVariantType(),
                    getIntersectionVariance(structuralVariant, similarVariants.get(0)));
        }
    }

    private List<StructuralVariant> findNearestStructuralVariants(StructuralVariant structuralVariant,
                                                                  Set<StructuralVariant> structuralVariants) {
        Map<Double, StructuralVariant> similarStructuralVariants = new TreeMap<>();

        for (StructuralVariant otherVariant : structuralVariants) {
            if (!(structuralVariant.getSrcChromosome() == otherVariant.getSrcChromosome() &&
                    structuralVariant.getDstChromosome() == otherVariant.getDstChromosome()))
                continue;

            int distanceVariance = getDistanceVariance(structuralVariant, otherVariant);
            double intersectionVariance = getIntersectionVariance(structuralVariant, otherVariant);

            if (onlyCommonGenes) {
                List<String> commonGenes = getCommonGenes(structuralVariant, otherVariant);
                if (commonGenes.size() < 1)
                    continue;
            }

            if ((distanceVarianceThreshold != null && distanceVariance > distanceVarianceThreshold) &&
                (intersectionVarianceThreshold != null && intersectionVariance > intersectionVarianceThreshold))
                continue;

            Double proportion = getSizeProportion(structuralVariant, otherVariant);
            if (minimalProportion != null && proportion != null && proportion < minimalProportion)
                continue;

            // In case of BND variant (Translocation), there is no information about variant size,
            // thus intersection variance score cannot be calculated. Distance variance score is used instead
            // for variant sorting.
            if (structuralVariant.getVariantType() == StructuralVariantType.BND)
                similarStructuralVariants.put(Double.valueOf(distanceVariance), otherVariant);
            else
                similarStructuralVariants.put(intersectionVariance, otherVariant);
        }

        return new ArrayList<>(similarStructuralVariants.values());
    }

    private int getDistanceVariance(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        int srcDist = Math.abs(structuralVariant.getSrcLoc() - otherVariant.getSrcLoc());
        int dstDist = Math.abs(structuralVariant.getDstLoc() - otherVariant.getDstLoc());
        int distanceVariance = srcDist + dstDist;

        return distanceVariance;
    }

    private double getIntersectionVariance(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        int from = Math.min(structuralVariant.getSrcLoc(), otherVariant.getSrcLoc());
        int to = Math.max(structuralVariant.getDstLoc(), otherVariant.getDstLoc());
        double intersectionVariance = (double)(to - from) / (double)(structuralVariant.getSize() + otherVariant.getSize());

        //Value of 0.5 means absolute match of variants. So just "normalize" it to 0.0 if there is absolute match.
        //Value of 0.5 for absolute match may be misleading
        return intersectionVariance - 0.5;
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

    private void addDistanceVarianceStatistics(String name, StructuralVariantType svType, int distanceVariance) throws Exception {
        String itemId = name + svType;
        StructuralVariantStatsItem item = structuralVariantStats.get(itemId);

        if (item == null) {
            throw new IllegalAccessException("Structural variant statistics item not initialized for id: " + itemId);
        }

        for (int distanceVarianceThreshold : distanceVarianceBasesCounts) {
            if (distanceVariance <= distanceVarianceThreshold)
                item.addStructuralVariantDistanceVariance(distanceVarianceThreshold);
        }
    }

    private void addIntersectionVarianceStats(String name, StructuralVariantType svType, Double intersectionVariance) throws Exception {
        String itemId = name + svType;
        StructuralVariantStatsItem item = structuralVariantStats.get(itemId);

        if (item == null) {
            throw new IllegalAccessException("Structural variant statistics item not initialized for id: " + itemId);
        }

        if (intersectionVarianceThresholds == null || intersectionVarianceThresholds.length == 0)
            return;

        for (double intersectionVarianceThreshold : intersectionVarianceThresholds) {
            if (intersectionVariance <= intersectionVarianceThreshold)
                item.addStructuralVariantIntersectionVariance(intersectionVarianceThreshold);
        }
    }

    private void printHeader(SvResultParser svParserMain, List<SvResultParser> svParserOthers) throws Exception {
        String svLabelMain = svParserMain.getName();

        String header =
                "sv_type\t" +
                "src_chr\t" +
                "dst_chr\t" +
                svLabelMain + "_filter\t" +
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
                svLabelOther + "_intersect_var\t" +
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
        String line = "";

        if (!mainPrinted) {
            line +=
                    svType.toString() + "\t" +
                    variant.getSrcChromosome().toString() + "\t" +
                    variant.getDstChromosome().toString() + "\t" +
                    StringUtils.join(variant.getFilters(), ";") + "\t" +
                    variant.getSrcLoc() + "\t" +
                    variant.getDstLoc() + "\t" +
                    variant.getSize() + "\t" +
                    getAllelicFraction(variant) + "\t" +
                    variant.getGene() + "\t" +
                    getVariantId(variant);


            mainPrinted = true;
        }

        if (similarVariants.size() > 0) {
            StructuralVariant similarStructuralVariant = similarVariants.get(0);

            int srcDist = Math.abs(variant.getSrcLoc() - similarStructuralVariant.getSrcLoc());
            int dstDist = Math.abs(variant.getDstLoc() - similarStructuralVariant.getDstLoc());

            String commonGenes = StringUtils.join(getCommonGenes(variant, similarStructuralVariant), ",");

            line += "\t" +
                    similarStructuralVariant.getSrcLoc() + "\t" +
                    similarStructuralVariant.getDstLoc() + "\t" +
                    similarStructuralVariant.getSize() + "\t" +
                    getAllelicFraction(similarStructuralVariant) + "\t" +
                    srcDist + "\t" +
                    dstDist + "\t" +
                    getDistanceVariance(variant, similarStructuralVariant) + "\t" +
                    getIntersectionVariance(variant, similarStructuralVariant) + "\t" +
                    similarStructuralVariant.getGene() + "\t" +
                    commonGenes + "\t" +
                    getSizeDifference(variant, similarStructuralVariant) + "\t" +
                    getSizeProportionAsString(variant, similarStructuralVariant) + "\t" +
                    getVariantId(similarStructuralVariant);
        }
        else
            line += "\t\t\t\t\t\t\t\t\t\t\t\t\t";

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

        return Integer.toString(other.getSize() - main.getSize());
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

    public void setIntersectionVarianceThresholds(double[] intersectionVarianceThresholds) {
        this.intersectionVarianceThresholds = intersectionVarianceThresholds;
    }

    public void setCalculateStructuralVariantStats(boolean calculateStructuralVariantStats) {
        this.calculateStructuralVariantStats = calculateStructuralVariantStats;
    }

    public void setExcludedRegions(List<ChromosomeRegion> excludedRegions) {
        this.excludedRegions = excludedRegions;
    }
}
