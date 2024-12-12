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
import java.util.*;
import java.util.stream.Collectors;

public class GeneAnnotator {
    private final Map<String, Gene> chr1 = new HashMap<>();
    private final Map<String, Gene> chr2 = new HashMap<>();
    private final Map<String, Gene> chr3 = new HashMap<>();
    private final Map<String, Gene> chr4 = new HashMap<>();
    private final Map<String, Gene> chr5 = new HashMap<>();
    private final Map<String, Gene> chr6 = new HashMap<>();
    private final Map<String, Gene> chr7 = new HashMap<>();
    private final Map<String, Gene> chr8 = new HashMap<>();
    private final Map<String, Gene> chr9 = new HashMap<>();
    private final Map<String, Gene> chr10 = new HashMap<>();
    private final Map<String, Gene> chr11 = new HashMap<>();
    private final Map<String, Gene> chr12 = new HashMap<>();
    private final Map<String, Gene> chr13 = new HashMap<>();
    private final Map<String, Gene> chr14 = new HashMap<>();
    private final Map<String, Gene> chr15 = new HashMap<>();
    private final Map<String, Gene> chr16 = new HashMap<>();
    private final Map<String, Gene> chr17 = new HashMap<>();
    private final Map<String, Gene> chr18 = new HashMap<>();
    private final Map<String, Gene> chr19 = new HashMap<>();
    private final Map<String, Gene> chr20 = new HashMap<>();
    private final Map<String, Gene> chr21 = new HashMap<>();
    private final Map<String, Gene> chr22 = new HashMap<>();
    private final Map<String, Gene> chrX = new HashMap<>();
    private final Map<String, Gene> chrY = new HashMap<>();
    private final Map<String, Gene> chrM = new HashMap<>();

    public void parseGeneFile(String filePath, String separator) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] header = reader.readLine().split(separator);

            String line;
            while((line = reader.readLine()) != null) {
                String[] values = line.split(separator);

                Gene gene = new Gene();
                gene.setSymbol(values[0].toUpperCase());
                gene.setChromosome(Chromosome.of(values[1]));
                gene.setStart(Integer.valueOf(values[2]));
                gene.setEnd(Integer.valueOf(values[3]));

                if (values.length > 4) {
                    List<String> infos = new ArrayList<>();
                    for (int i = 4; i < values.length; i++) {
                        String label = header[i].replaceAll("\t", "  ");
                        String value = values[i].replaceAll(";", ",").replaceAll(":", ".")
                                .replaceAll("\"", "'").replaceAll("\t", "  ");

                        infos.add(label + "=" + value);
                    }

                    gene.setInfo(StringUtils.join(infos, ";").replaceAll("\\|", "-"));
                }

                addGene(gene);
            }
        }
    }

    private void addGene(Gene gene) {
        getChromosomeGenes(gene.getChromosome()).put(gene.getSymbol(), gene);
    }

    public List<Gene> findIntersectingGenes(StructuralVariant variant) {
        List<Gene> intersectingGenes = new ArrayList<>();

        // inter-chromosomal translocation
        if (variant.getSrcChromosome() != variant.getDstChromosome()) {
            Map<String, Gene> sourceGenes = getChromosomeGenes(variant.getSrcChromosome());
            Map<String, Gene> destinationGenes = getChromosomeGenes(variant.getDstChromosome());

            for (Gene gene : sourceGenes.values())
                if (gene.getStart() <= variant.getSrcLoc() && gene.getEnd() >= variant.getSrcLoc())
                    intersectingGenes.add(gene);


            for (Gene gene : destinationGenes.values())
                if (gene.getStart() <= variant.getDstLoc() && gene.getEnd() >= variant.getDstLoc())
                    intersectingGenes.add(gene);
        }
        // other variants - always on same chromosome
        else {
            int start = Math.min(variant.getSrcLoc(), variant.getDstLoc());
            int end = Math.max(variant.getSrcLoc(), variant.getDstLoc());

            Map<String, Gene> genes = getChromosomeGenes(variant.getSrcChromosome());

            for (Gene gene : genes.values()) {
                if (!(start > gene.getEnd() || end < gene.getStart()))
                    intersectingGenes.add(gene);
            }
        }

        return intersectingGenes;
    }

    private Map<String, Gene> getChromosomeGenes(Chromosome chromosome) {
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

        return Collections.emptyMap();
    }

    public static Set<String> toSymbols(List<Gene> genes) {
        return genes.stream()
                        .map(Gene::getSymbol)
                        .collect(Collectors.toSet());
    }

    public Gene getGene(String symbol, Chromosome chromosome) {
        return  getChromosomeGenes(chromosome).get(symbol.toUpperCase());
    }
}
