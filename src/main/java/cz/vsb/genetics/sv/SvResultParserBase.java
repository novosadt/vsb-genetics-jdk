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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SvResultParserBase implements SvResultParser {
    protected final Set<StructuralVariant> translocations = new HashSet<>();
    protected final Set<StructuralVariant> duplications = new HashSet<>();
    protected final Set<StructuralVariant> inversions = new HashSet<>();
    protected final Set<StructuralVariant> deletions = new HashSet<>();
    protected final Set<StructuralVariant> insertions = new HashSet<>();
    protected final Set<StructuralVariant> copyNumberVariations = new HashSet<>();
    protected final Set<StructuralVariant> unknown = new HashSet<>();

    private boolean removeDuplicateVariants = false;

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

    protected void addStructuralVariant(StructuralVariant variant, Set<StructuralVariant> variants, StructuralVariantType svType) {
        variant.setVariantType(svType);

        if (!removeDuplicateVariants || !variants.contains(variant))
            variants.add(variant);
    }
}
