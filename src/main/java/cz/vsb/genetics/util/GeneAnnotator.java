/*
 * Copyright (C) 2024  Tomas Novosad
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

package cz.vsb.genetics.util;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.common.Gene;
import cz.vsb.genetics.sv.StructuralVariant;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GeneAnnotator {
    private final List<Gene> chr1 = new ArrayList<>();
    private final List<Gene> chr2 = new ArrayList<>();
    private final List<Gene> chr3 = new ArrayList<>();
    private final List<Gene> chr4 = new ArrayList<>();
    private final List<Gene> chr5 = new ArrayList<>();
    private final List<Gene> chr6 = new ArrayList<>();
    private final List<Gene> chr7 = new ArrayList<>();
    private final List<Gene> chr8 = new ArrayList<>();
    private final List<Gene> chr9 = new ArrayList<>();
    private final List<Gene> chr10 = new ArrayList<>();
    private final List<Gene> chr11 = new ArrayList<>();
    private final List<Gene> chr12 = new ArrayList<>();
    private final List<Gene> chr13 = new ArrayList<>();
    private final List<Gene> chr14 = new ArrayList<>();
    private final List<Gene> chr15 = new ArrayList<>();
    private final List<Gene> chr16 = new ArrayList<>();
    private final List<Gene> chr17 = new ArrayList<>();
    private final List<Gene> chr18 = new ArrayList<>();
    private final List<Gene> chr19 = new ArrayList<>();
    private final List<Gene> chr20 = new ArrayList<>();
    private final List<Gene> chr21 = new ArrayList<>();
    private final List<Gene> chr22 = new ArrayList<>();
    private final List<Gene> chrX = new ArrayList<>();
    private final List<Gene> chrY = new ArrayList<>();
    private final List<Gene> chrM = new ArrayList<>();

    public void parseGeneFile(String filePath, String separator, boolean hasHeaderRow) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            if (hasHeaderRow)
                reader.readLine(); // skip header line

            String line;
            while((line = reader.readLine()) != null) {
                String[] values = line.split(separator);

                Gene gene = new Gene();
                gene.setSymbol(values[0]);
                gene.setChromosome(Chromosome.of(values[1]));
                gene.setStart(Integer.valueOf(values[2]));
                gene.setEnd(Integer.valueOf(values[3]));

                if (values.length > 4)
                    gene.setName(values[4]);

                if (values.length > 5)
                    gene.setId(values[5]);

                addGene(gene);
            }
        }
    }

    private void addGene(Gene gene) {
        getChromosomeGenes(gene.getChromosome()).add(gene);
    }

    public List<Gene> findIntersectingGenes(StructuralVariant variant) {
        List<Gene> intersectingGenes = new ArrayList<>();

        // inter-chromosomal translocation
        if (variant.getSrcChromosome() != variant.getDstChromosome()) {
            List<Gene> sourceGenes = getChromosomeGenes(variant.getSrcChromosome());
            List<Gene> destinationGenes = getChromosomeGenes(variant.getDstChromosome());

            for (Gene gene : sourceGenes)
                if (gene.getStart() <= variant.getSrcLoc() && gene.getEnd() >= variant.getSrcLoc())
                    intersectingGenes.add(gene);


            for (Gene gene : destinationGenes)
                if (gene.getStart() <= variant.getDstLoc() && gene.getEnd() >= variant.getDstLoc())
                    intersectingGenes.add(gene);
        }
        // other variants - always on same chromosome
        else {
            int start = Math.min(variant.getSrcLoc(), variant.getDstLoc());
            int end = Math.max(variant.getSrcLoc(), variant.getDstLoc());

            List<Gene> genes = getChromosomeGenes(variant.getSrcChromosome());

            for (Gene gene : genes) {
                if (!(start > gene.getEnd() || end < gene.getStart()))
                    intersectingGenes.add(gene);
            }
        }

        return intersectingGenes;
    }

    private List<Gene> getChromosomeGenes(Chromosome chromosome) {
        switch (chromosome) {
            case chr1: return this.chr1;
            case chr2: return this.chr2;
            case chr3: return this.chr3;
            case chr4: return this.chr4;
            case chr5: return this.chr5;
            case chr6: return this.chr6;
            case chr7: return this.chr7;
            case chr8: return this.chr8;
            case chr9: return this.chr9;
            case chr10: return this.chr10;
            case chr11: return this.chr11;
            case chr12: return this.chr12;
            case chr13: return this.chr13;
            case chr14: return this.chr14;
            case chr15: return this.chr15;
            case chr16: return this.chr16;
            case chr17: return this.chr17;
            case chr18: return this.chr18;
            case chr19: return this.chr19;
            case chr20: return this.chr20;
            case chr21: return this.chr21;
            case chr22: return this.chr22;
            case chrX: return this.chrX;
            case chrY: return this.chrY;
            case chrM: return this.chrM;
        }

        return Collections.emptyList();
    }

    public static String toSymbols(List<Gene> genes) {
        return StringUtils.join(genes.stream()
                        .map(Gene::getSymbol)
                        .collect(Collectors.toList()), ";");
    }
}
