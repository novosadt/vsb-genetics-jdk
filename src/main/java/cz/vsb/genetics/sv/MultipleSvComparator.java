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
    private final Map<String, StructuralVariantStatsItem> structuralVariantStats = new LinkedHashMap<>();
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
            
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.BND, name), new StructuralVariantStatsItem(name, StructuralVariantType.BND));
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.INV, name), new StructuralVariantStatsItem(name, StructuralVariantType.INV));
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.DUP, name), new StructuralVariantStatsItem(name, StructuralVariantType.DUP));
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.DEL, name), new StructuralVariantStatsItem(name, StructuralVariantType.DEL));
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.INS, name), new StructuralVariantStatsItem(name, StructuralVariantType.INS));
            structuralVariantStats.put(getStatsItemId(StructuralVariantType.UNK, name), new StructuralVariantStatsItem(name, StructuralVariantType.UNK));
        }
    }

    private static String getStatsItemId(StructuralVariantType svType, String name) {
        return name + svType;
    }

    public void saveStructuralVariantStats(String outputFile) throws Exception {
        if (structuralVariantStats.size() == 0)
            return;

        List<String> headers = new ArrayList<>();
        headers.add("name");
        headers.add("sv_type");
        headers.add("sv_count_total");
        headers.add("sv_count_passed");
        headers.add("sv_count_passed_pct");

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
                record.add(String.valueOf(item.getSvCountPassed()));
                record.add(String.format("%.2f", item.getSvCountTotal() == 0 ? 0.0 : (double) (item.getSvCountPassed()) / (double) item.getSvCountTotal() * 100.0));

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
            if (processedVariants.contains(structuralVariant))
                continue;

            processedVariants.add(structuralVariant);
            applyMainVariantFilters(structuralVariant);

            if (!structuralVariant.isFiltered())
                unfilteredSvCount++;

            for (int i = 0; i < otherParsersVariants.size(); i++) {
                Set<StructuralVariant> others = otherParsersVariants.get(i);
                StructuralVariant similarVariant = findBestSimilarStructuralVariant(structuralVariant, others);

                printSimilarVariant(structuralVariant, similarVariant, svType);

                if (similarVariant != null) {
                    similarVariantCounts[i]++;
                    addStructuralVariantStats(structuralVariant, i, similarVariant);
                }
            }

            mainPrinted = false;
            fileWriter.write("\n");
        }

        setStatsSvCountTotal(unfilteredSvCount, svType);
        printSimpleVariantsStatistics(mainVariants, otherParsersVariants, svType, similarVariantCounts);
    }

    private void applyMainVariantFilters(StructuralVariant variant) {
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

    private StructuralVariant findBestSimilarStructuralVariant(StructuralVariant structuralVariant,
                                                               Set<StructuralVariant> structuralVariants) {
        TreeMap<Double, StructuralVariant> similarStructuralVariants = new TreeMap<>();

        for (StructuralVariant otherVariant : structuralVariants) {
            if (!(structuralVariant.getSrcChromosome() == otherVariant.getSrcChromosome() &&
                    structuralVariant.getDstChromosome() == otherVariant.getDstChromosome()))
                continue;

            int distanceVariance = getDistanceVariance(structuralVariant, otherVariant);
            double intersectionVariance = getIntersectionVariance(structuralVariant, otherVariant);

            // In case of BND variant (Translocation), there is no information about variant size,
            // thus intersection variance score cannot be calculated. Distance variance score is used instead
            // for variant sorting.
            if (structuralVariant.getVariantType() == StructuralVariantType.BND)
                similarStructuralVariants.put(Double.valueOf(distanceVariance), otherVariant);
            else
                similarStructuralVariants.put(intersectionVariance, otherVariant);
        }

        return similarStructuralVariants.size() > 0 ? similarStructuralVariants.firstEntry().getValue() : null;
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
                    svLabelOther + "_filter\t" +
                    svLabelOther + "_filter_name\t" +
                    svLabelOther + "_id";
        }

        fileWriter.write(header + "\n");
    }

    private void printSimilarVariant(StructuralVariant variant, StructuralVariant similarVariant,
                                     StructuralVariantType svType) throws Exception {
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

        if (similarVariant != null) {
            applySimilarVariantFilters(variant, similarVariant);

            int srcDist = Math.abs(variant.getSrcLoc() - similarVariant.getSrcLoc());
            int dstDist = Math.abs(variant.getDstLoc() - similarVariant.getDstLoc());

            String commonGenes = StringUtils.join(getCommonGenes(variant, similarVariant), ",");

            line += "\t" +
                    similarVariant.getSrcLoc() + "\t" +
                    similarVariant.getDstLoc() + "\t" +
                    similarVariant.getSize() + "\t" +
                    getAllelicFraction(similarVariant) + "\t" +
                    srcDist + "\t" +
                    dstDist + "\t" +
                    getDistanceVariance(variant, similarVariant) + "\t" +
                    getIntersectionVariance(variant, similarVariant) + "\t" +
                    similarVariant.getGene() + "\t" +
                    commonGenes + "\t" +
                    getSizeDifference(variant, similarVariant) + "\t" +
                    getSizeProportionAsString(variant, similarVariant) + "\t" +
                    (similarVariant.passed() ? "PASS" : "") + "\t" +
                    StringUtils.join(similarVariant.getFilters(), ",") + "\t" +
                    getVariantId(similarVariant);
        }
        else
            line += "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

        fileWriter.write(line);
    }

    private void addStructuralVariantStats(StructuralVariant structuralVariant, int otherParserIndex, StructuralVariant similarVariant) throws Exception {
        if (!calculateStructuralVariantStats || structuralVariant.isFiltered())
            return;

        String parserName = svParserOthers.get(otherParserIndex).getName();

        addPassedStatistics(parserName, structuralVariant, similarVariant);

        if (!similarVariant.passed())
            return;

        if (distanceVarianceBasesCounts != null) {
            addDistanceVarianceStatistics(parserName, structuralVariant.getVariantType(), getDistanceVariance(structuralVariant, similarVariant));
        }

        if (intersectionVarianceThresholds != null) {
            addIntersectionVarianceStats(parserName, structuralVariant.getVariantType(), getIntersectionVariance(structuralVariant, similarVariant));
        }
    }

    private void setStatsSvCountTotal(int count, StructuralVariantType svType) {
        if (!calculateStructuralVariantStats)
            return;

        for (SvResultParser svResultParser : svParserOthers) {
            String name = svResultParser.getName();

            structuralVariantStats.get(getStatsItemId(svType, name)).setSvCountTotal(count);
        }
    }

    private void applySimilarVariantFilters(StructuralVariant structuralVariant, StructuralVariant otherVariant) {
        otherVariant.resetFilter();

        int distanceVariance = getDistanceVariance(structuralVariant, otherVariant);
        double intersectionVariance = getIntersectionVariance(structuralVariant, otherVariant);

        boolean commonGenesFilter = false;
        if (onlyCommonGenes) {
            List<String> commonGenes = getCommonGenes(structuralVariant, otherVariant);
            if (commonGenes.size() < 1)
                commonGenesFilter = true;
        }

        // distance variance filter
        boolean distanceVarianceFilter = distanceVarianceThreshold != null && distanceVariance > distanceVarianceThreshold;

        // intersection variance filter
        boolean intersectionVarianceFilter = intersectionVarianceThreshold != null && (Double.isInfinite(intersectionVariance) || intersectionVariance > intersectionVarianceThreshold);

        // proportion filter
        Double proportion = getSizeProportion(structuralVariant, otherVariant);
        boolean sizeProportionFilter = minimalProportion != null && proportion != null && proportion < minimalProportion;

        if (commonGenesFilter)
            otherVariant.addFilter(StructuralVariantFilter.COMMON_GENES);

        if (distanceVarianceFilter)
            otherVariant.addFilter(StructuralVariantFilter.DISTANCE_VARIANCE);

        if (intersectionVarianceFilter)
            otherVariant.addFilter(StructuralVariantFilter.INTERSECTION_VARIANCE);

        if (sizeProportionFilter)
            otherVariant.addFilter(StructuralVariantFilter.SIZE_PROPORTION);

        otherVariant.setPassed(!sizeProportionFilter && (!distanceVarianceFilter || !intersectionVarianceFilter));
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
        double intersectionVariance = (double)(to - from + 1) / (double)(structuralVariant.getSize() + otherVariant.getSize());

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
        String itemId = getStatsItemId(svType, name);
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
        String itemId = getStatsItemId(svType, name);
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

    private void addPassedStatistics(String name, StructuralVariant StructuralVariant, StructuralVariant similarVariant) throws Exception {
        if (!calculateStructuralVariantStats)
            return;

        String itemId = getStatsItemId(similarVariant.getVariantType(), name);
        StructuralVariantStatsItem item = structuralVariantStats.get(itemId);

        if (item == null) {
            throw new IllegalAccessException("Structural variant statistics item not initialized for id: " + itemId);
        }

        applySimilarVariantFilters(StructuralVariant, similarVariant);

        if (similarVariant.passed())
            item.addSvCountPassed();
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
