package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.om.sv.BionanoPipelineResultParser;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.SvResultParser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;

public class SamplotBionanoVariantGenerator {
    public void generate(SvResultParser bionanoParser, String samplotVariantFile, String samplotCmdBase,
                         String samplotWorkdir, int forks) throws Exception {

        List<String> commands = new ArrayList<>();
        List<String> variantFiles = new ArrayList<>();

        for (StructuralVariant variant : bionanoParser.getVariants()) {
            String variantFileCsv = samplotWorkdir + "/" + variant.getId() + ".samplot.csv";

            String command = String.format("%s -n %s --sv_file_name %s -o %s -c %d -s %d -e %d -t %s",
                    samplotCmdBase,
                    variant.getId(),
                    variantFileCsv,
                    variantFileCsv.replace(".csv", ".png"),
                    variant.getSrcChromosome().number,
                    variant.getSrcLoc(),
                    variant.getDstLoc(),
                    variant.getVariantType().toString());

            commands.add(command);
            variantFiles.add(variantFileCsv);
        }

        processCommands(commands, forks);
        assemblyVariants(variantFiles, samplotVariantFile);
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
            List<String> lines = Files.readAllLines(path);

            if (!headerWritten) {
                Files.write(output, Collections.singletonList(lines.get(0)), charset, StandardOpenOption.CREATE);
                headerWritten = true;
            }

            lines.remove(0);

            Files.write(output, new ArrayList<>(new HashSet<>(lines)), charset, StandardOpenOption.APPEND);
        }
    }
}
