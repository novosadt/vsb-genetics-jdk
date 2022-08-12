package cz.vsb.genetics.ngs.sv;

import cz.vsb.genetics.om.sv.BionanoPipelineResultParser;
import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.SvResultParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SamplotBionanoVariantGenerator {
    public void generate(SvResultParser bionanoParser, String samplotVariantFile, String samplotCmdBase,
                         String samplotWorkdir, int forks) throws Exception {

        List<String> commands = new ArrayList<>();

        for (StructuralVariant variant : bionanoParser.getVariants()) {
            String command = String.format("%s -n %s --sv_file_name %s.samplot.csv -o %s.samplot.png -c %d -s %d -e %d -t %s",
                    samplotCmdBase,
                    variant.getId(),
                    samplotWorkdir + "/" + variant.getId(),
                    samplotWorkdir + "/" + variant.getId(),
                    variant.getSrcChromosome().number,
                    variant.getSrcLoc(),
                    variant.getDstLoc(),
                    variant.getVariantType().toString());

            commands.add(command);
        }

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

    private void processCommands(List<String> commands, String batchName) throws Exception {
        for (String command : commands) {
            System.out.println("Exec: " + batchName + " " + command);

            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        }
    }
}
