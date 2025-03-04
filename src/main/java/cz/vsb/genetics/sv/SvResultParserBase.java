/*
 * Copyright (C) 2025  Tomas Novosad
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

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.util.GeneAnnotator;

import java.util.*;

public abstract class SvResultParserBase implements SvResultParser {
    protected final Set<StructuralVariant> translocations = new HashSet<>();
    protected final Set<StructuralVariant> duplications = new HashSet<>();
    protected final Set<StructuralVariant> inversions = new HashSet<>();
    protected final Set<StructuralVariant> deletions = new HashSet<>();
    protected final Set<StructuralVariant> insertions = new HashSet<>();
    protected final Set<StructuralVariant> copyNumberVariations = new HashSet<>();
    protected final Set<StructuralVariant> unknown = new HashSet<>();

    private boolean removeDuplicateVariants = false;

    protected String name;
    protected String[] infoTags = new String[0];
    protected GeneAnnotator geneAnnotator;

    protected void reset() {
        translocations.clear();
        duplications.clear();
        inversions.clear();
        deletions.clear();
        insertions.clear();
        copyNumberVariations.clear();
        unknown.clear();
    }

    @Override
    public List<StructuralVariant> getVariants() {
        List<StructuralVariant> variants = new ArrayList<>();

        variants.addAll(getTranslocations());
        variants.addAll(getDuplications());
        variants.addAll(getInversions());
        variants.addAll(getDeletions());
        variants.addAll(getInsertions());
        variants.addAll(getCopyNumberVariations());
        variants.addAll(getUnknown());

        return variants;
    }

    @Override
    public Set<StructuralVariant> getTranslocations() {
        return translocations;
    }

    @Override
    public Set<StructuralVariant> getDuplications() {
        return duplications;
    }

    @Override
    public Set<StructuralVariant> getInversions() {
        return inversions;
    }

    @Override
    public Set<StructuralVariant> getDeletions() {
        return deletions;
    }

    @Override
    public Set<StructuralVariant> getInsertions() {
        return insertions;
    }

    @Override
    public Set<StructuralVariant> getCopyNumberVariations() {
        return copyNumberVariations;
    }

    @Override
    public Set<StructuralVariant> getUnknown() {
        return unknown;
    }

    @Override
    public void setRemoveDuplicateVariants(boolean removeDuplicateVariants) {
        this.removeDuplicateVariants = removeDuplicateVariants;
    }

    protected void printStructuralVariantStats(String parserName) {
        System.out.println();
        System.out.println(parserName + " statistics:");
        System.out.println("Translocations (BND):\t" + getTranslocations().size());
        System.out.println("Duplications (DUP):\t\t" + getDuplications().size());
        System.out.println("Inversions (INV):\t\t" + getInversions().size());
        System.out.println("Deletions (DEL):\t\t" + getDeletions().size());
        System.out.println("Insertions (INS):\t\t" + getInsertions().size());
        System.out.println("Copy number var. (CNV):\t" + getCopyNumberVariations().size());
        System.out.println("Unknown SV type (UNK):\t" + getUnknown().size());
    }

    protected boolean addStructuralVariant(StructuralVariant variant, Set<StructuralVariant> variants, StructuralVariantType svType) {
        variant.setVariantType(svType);

        // In case of translocation sort chromosomes
        if (variant.getVariantType() == StructuralVariantType.BND) {
            Chromosome srcChromosome = variant.getSrcChromosome();
            Chromosome dstChromosome = variant.getDstChromosome();
            int srcLoc = variant.getSrcLoc();
            int dstLoc = variant.getDstLoc();

            if (srcChromosome.number > dstChromosome.number) {
                variant.setSrcChromosome(dstChromosome);
                variant.setDstChromosome(srcChromosome);
                variant.setSrcLoc(dstLoc);
                variant.setDstLoc(srcLoc);
            }
        }

        if (removeDuplicateVariants && variants.contains(variant))
            return false;

        variants.add(variant);

        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setInfoTags(String[] infoTags) {
        this.infoTags = infoTags == null ? new String[0] : infoTags;
    }

    @Override
    public String[] getInfoTags() {
        return infoTags;
    }

    @Override
    public void setGeneAnnotator(GeneAnnotator geneAnnotator) {
        this.geneAnnotator = geneAnnotator;
    }
}
