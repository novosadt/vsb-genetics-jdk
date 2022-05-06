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

public class StructuralVariant {
    private final Chromosome srcChromosome;
    private final Long srcLoc;
    private final Chromosome dstChromosome;
    private final Long dstLoc;
    private final Long size;
    private final String gene;
    private Double frequency;
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
        if (!(o instanceof StructuralVariant)) return false;

        StructuralVariant that = (StructuralVariant) o;

        if (getSrcChromosome() != that.getSrcChromosome()) return false;
        if (!getSrcLoc().equals(that.getSrcLoc())) return false;
        if (getDstChromosome() != that.getDstChromosome()) return false;
        if (!getDstLoc().equals(that.getDstLoc())) return false;
        if (!getSize().equals(that.getSize())) return false;
        return getVariantType() == that.getVariantType();
    }

    @Override
    public int hashCode() {
        int result = getSrcChromosome().hashCode();
        result = 31 * result + getSrcLoc().hashCode();
        result = 31 * result + getDstChromosome().hashCode();
        result = 31 * result + getDstLoc().hashCode();
        result = 31 * result + getSize().hashCode();
        result = 31 * result + getVariantType().hashCode();
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

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
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
}
