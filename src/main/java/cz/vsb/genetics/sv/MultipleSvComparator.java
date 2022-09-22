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

import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.util.*;

public class MultipleSvComparator {
    private FileWriter fileWriter;
    private boolean onlyCommonGenes = false;
    private Long variantDistance = null;
    private Double minimalProportion = null;
    private Set<StructuralVariantType> svTypes;
    private String svLabelMain;
    private List<String> svLabelOthers;
    private SvResultParser svParserMain;
    private List<SvResultParser> svParserOthers;
    private boolean mainPrinted;

    public void compareStructuralVariants(SvResultParser svParserMain, String svLabelMain, List<SvResultParser> svParserOthers,
                                          List<String> svLabelOthers, String outputFile) throws Exception {
        fileWriter = new FileWriter(outputFile);
        this.svLabelMain = svLabelMain;
        this.svLabelOthers = svLabelOthers;
        this.svParserMain = svParserMain;
        this.svParserOthers = svParserOthers;

        printHeader(svLabelMain, svLabelOthers);

        mainPrinted = false;
        
        processStructuralVariants(StructuralVariantType.BND);
        processStructuralVariants(StructuralVariantType.INV);
        processStructuralVariants(StructuralVariantType.DUP);
        processStructuralVariants(StructuralVariantType.DEL);
        processStructuralVariants(StructuralVariantType.INS);
        processStructuralVariants(StructuralVariantType.UNK);

        fileWriter.close();
    }

    public boolean isOnlyCommonGenes() {
        return onlyCommonGenes;
    }

    public void setOnlyCommonGenes(boolean onlyCommonGenes) {
        this.onlyCommonGenes = onlyCommonGenes;
    }

    public Long getVariantDistance() {
        return variantDistance;
    }

    public void setVariantDistance(Long variantDistance) {
        this.variantDistance = variantDistance;
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
        for (int i = 0; i < svParserOthers.size(); i++) {
            SvResultParser other = svParserOthers.get(i);
            String labelOther = svLabelOthers.get(i);

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

    private void processStructuralVariants(Set<StructuralVariant> structuralVariants1, List<Set<StructuralVariant>> structuralVariants2,
                                           StructuralVariantType svType) throws Exception {
        Set<StructuralVariant> processedVariants = new HashSet<>();

        if (svTypes != null && !svTypes.contains(svType))
            return;

        int[] similarVariantCounts = new int[structuralVariants2.size()];

        for (StructuralVariant structuralVariant : structuralVariants1) {
            if (!processedVariants.contains(structuralVariant))
                processedVariants.add(structuralVariant);
            else
                continue;

            int similarVariantsTotal = 0;
            for (int i = 0; i < structuralVariants2.size(); i++) {
                Set<StructuralVariant> others = structuralVariants2.get(i);

                List<StructuralVariant> similarVariants = findNearestStructuralVariants(structuralVariant, others);
                similarVariantsTotal += similarVariants.size();

                if (similarVariants.size() == 0)
                    continue;

                printSimilarVariants(structuralVariant, similarVariants, svType);
                similarVariantCounts[i]++;
            }

            mainPrinted = false;

            if (similarVariantsTotal != 0)
                fileWriter.write("\n");
        }

        for (int i = 0; i < structuralVariants2.size(); i++) {
            double percentage = structuralVariants1.size() == 0 ? 0.0 :
                    (double) similarVariantCounts[i] / (double) structuralVariants1.size() * 100.0;

            System.out.printf("Common SV (%s with %s / %s) - %s:\t%d/%d (%.02f%%)%n", svLabelMain, svLabelOthers.get(i), svLabelMain, svType.toString(),
                    similarVariantCounts[i], structuralVariants1.size(), percentage);
        }

    }

    private List<StructuralVariant> findNearestStructuralVariants(StructuralVariant structuralVariant,
            Set<StructuralVariant> structuralVariants) {
        Map<Long, StructuralVariant> similarStructuralVariants = new TreeMap<>();

        for (StructuralVariant otherVariant : structuralVariants) {
            if (!(structuralVariant.getSrcChromosome() == otherVariant.getSrcChromosome() &&
                    structuralVariant.getDstChromosome() == otherVariant.getDstChromosome()))
                continue;

            Long srcDist = Math.abs(structuralVariant.getSrcLoc() - otherVariant.getSrcLoc());
            Long dstDist = Math.abs(structuralVariant.getDstLoc() - otherVariant.getDstLoc());
            Long distSum = srcDist + dstDist;

            if (onlyCommonGenes) {
                List<String> commonGenes = getCommonGenes(structuralVariant, otherVariant);
                if (commonGenes.size() < 1)
                    continue;
            }

            if (variantDistance != null && distSum > variantDistance)
                continue;

            Double proportion = getSizeProportion(structuralVariant, otherVariant);
            if (minimalProportion != null && proportion != null && proportion < minimalProportion)
                continue;

            similarStructuralVariants.put(distSum, otherVariant);
        }

        return new ArrayList<>(similarStructuralVariants.values());
    }

    private void printHeader(String svLabelMain, List<String> svLabelOthers) throws Exception {
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

        for (String svLabelOther : svLabelOthers) {
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

        return new Long(other.getSize() - main.getSize()).toString();
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
}
