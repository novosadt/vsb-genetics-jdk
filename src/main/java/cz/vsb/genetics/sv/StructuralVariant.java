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

import cz.vsb.genetics.common.Chromosome;

import java.util.Map;

public class StructuralVariant {
    private String id;
    private final Chromosome srcChromosome;
    private final Long srcLoc;
    private final Chromosome dstChromosome;
    private final Long dstLoc;
    private final Long size;
    private final String gene;
    private Double variantAlleleFraction;
    private Map<String, String> info;
    private StructuralVariantType variantType;
    private StructuralVariantOrientation srcOrientation;
    private StructuralVariantOrientation dstOrientation;

    public StructuralVariant(Chromosome srcChromosome, Long srcLoc, Chromosome dstChromosome, Long dstLoc,
                             Long size, String gene) {
        this.srcChromosome = srcChromosome;
        this.srcLoc = srcLoc;
        this.dstChromosome = dstChromosome;
        this.dstLoc = dstLoc;
        this.size = size;
        this.gene = gene;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructuralVariant that = (StructuralVariant) o;

        if (srcChromosome != that.srcChromosome) return false;
        if (!srcLoc.equals(that.srcLoc)) return false;
        if (dstChromosome != that.dstChromosome) return false;
        if (!dstLoc.equals(that.dstLoc)) return false;
        if (!size.equals(that.size)) return false;
        if (variantType != that.variantType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = srcChromosome.hashCode();
        result = 31 * result + srcLoc.hashCode();
        result = 31 * result + dstChromosome.hashCode();
        result = 31 * result + dstLoc.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + variantType.hashCode();
        return result;
    }

    public Chromosome getSrcChromosome() {
        return srcChromosome;
    }

    public Long getSrcLoc() {
        return srcLoc;
    }

    public Chromosome getDstChromosome() {
        return dstChromosome;
    }

    public Long getDstLoc() {
        return dstLoc;
    }

    public Long getSize() {
        return size;
    }

    public String getGene() {
        return gene;
    }

    public StructuralVariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(StructuralVariantType variantType) {
        this.variantType = variantType;
    }

    public Double getVariantAlleleFraction() {
        return variantAlleleFraction;
    }

    public void setVariantAlleleFraction(Double variantAlleleFraction) {
        this.variantAlleleFraction = variantAlleleFraction;
    }

    public StructuralVariantOrientation getSrcOrientation() {
        return srcOrientation;
    }

    public void setSrcOrientation(StructuralVariantOrientation srcOrientation) {
        this.srcOrientation = srcOrientation;
    }

    public StructuralVariantOrientation getDstOrientation() {
        return dstOrientation;
    }

    public void setDstOrientation(StructuralVariantOrientation dstOrientation) {
        this.dstOrientation = dstOrientation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }
}
