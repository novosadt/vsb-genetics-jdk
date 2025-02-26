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


package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.common.Chromosome;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SamplotBionanoVariantGenerator {
    private String outputImageFormat = "png";

    public void generate(SvResultParser bionanoParser, String samplotVariantFile, String samplotCmdBase,
                         String samplotWorkdir, int forks) throws Exception {

        List<String> commands = new ArrayList<>();
        List<String> variantFiles = new ArrayList<>();

        for (StructuralVariant variant : bionanoParser.getVariants()) {
            String variantFileCsv = samplotWorkdir + "/" + variant.getId() + ".samplot.csv";

            String command = getSamplotCommand(samplotCmdBase, variant, variantFileCsv);

            commands.add(command);
            variantFiles.add(variantFileCsv);
        }

        processCommands(commands, forks);
        assemblyVariants(variantFiles, samplotVariantFile);
    }

    private String getSamplotCommand(String samplotCmdBase, StructuralVariant variant, String variantFileCsv) {
        if (variant.getVariantType() == StructuralVariantType.BND) {
            return String.format("%s -n %s --sv_file_name %s -o %s -c %s -s %d -e %d -c %s -s %d -e %d -t %s --zoom 10000",
                    samplotCmdBase,
                    variant.getId(),
                    variantFileCsv,
                    variantFileCsv.replace(".csv", "." + outputImageFormat),
                    getChromosomeForCommand(variant.getSrcChromosome()),
                    variant.getSrcLoc(),
                    variant.getSrcLoc(),
                    getChromosomeForCommand(variant.getDstChromosome()),
                    variant.getDstLoc(),
                    variant.getDstLoc(),
                    variant.getVariantType().toString());
        }

        return String.format("%s -n %s --sv_file_name %s -o %s -c %s -s %d -e %d -t %s",
                samplotCmdBase,
                variant.getId(),
                variantFileCsv,
                variantFileCsv.replace(".csv", "." + outputImageFormat),
                getChromosomeForCommand(variant.getSrcChromosome()),
                variant.getSrcLoc(),
                variant.getDstLoc(),
                variant.getVariantType().toString());
    }

    private void processCommands(List<String> commands, int forks) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(forks);
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (String command : commands) {
            tasks.add(() -> {
                try {
                    System.out.println("Exec: " + command);
                    Process p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return 0;
            });
        }

        executorService.invokeAll(tasks);
        executorService.shutdown();
    }

    private void assemblyVariants(List<String> variantFiles, String samplotVariantFile) throws Exception {
        Path output = Paths.get(samplotVariantFile);

        boolean headerWritten = false;
        Charset charset = StandardCharsets.UTF_8;

        for (String variantFile : variantFiles) {
            Path path = Paths.get(variantFile);

            if (!Files.exists(path)) {
                System.out.println("Samplot variant info: " + variantFile + " missing. A problem with samplot tool - skipping. Try to execute Samplot command alone to determine the problem.");
                continue;
            }

            List<String> lines = Files.readAllLines(path);

            if (!headerWritten) {
                Files.write(output, Collections.singletonList(lines.get(0)), charset, StandardOpenOption.CREATE);
                headerWritten = true;
            }

            lines.remove(0);

            Files.write(output, new ArrayList<>(new HashSet<>(lines)), charset, StandardOpenOption.APPEND);
        }
    }

    private String getChromosomeForCommand(Chromosome chromosome) {
        if (chromosome == Chromosome.chrX)
            return "X";

        if (chromosome == Chromosome.chrY)
            return "Y";

        return Integer.toString(chromosome.number);
    }

    public void setOutputImageFormat(String outputImageFormat) {
        this.outputImageFormat = outputImageFormat;
    }
}
