package cz.vsb.genetics.om.bionano;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SMapParser {
    private String[] header;

    public SMap parse(String file) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        SMap smap = new SMap();

        String line;
        while((line = reader.readLine()) != null) {
            if (line.startsWith("#h")) {
                line = line.replace("#h", "").trim();
                header = line.split("\t");
                continue;
            }

            if (line.startsWith("#") || StringUtils.isBlank(line))
                continue;

            SMapEntry entry = parseLine(line);

            smap.add(entry);
        }

        reader.close();

        return smap;
    }

    private SMapEntry parseLine(String line) {
        String[] tmp = line.split("\t");

        assert tmp.length == header.length;

        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < tmp.length; i++)
            values.put(header[i], tmp[i]);

        SMapEntry entry = new SMapEntry(values.get("SmapEntryID") != null ? new Integer(values.get("SmapEntryID")) : null);
        entry.setQryContigID(values.get("QryContigID") != null ? new Integer(values.get("QryContigID")) : null);
        entry.setRefcontigID1(values.get("RefcontigID1") != null ? new Integer(values.get("RefcontigID1")) : null);
        entry.setRefcontigID2(values.get("RefcontigID2") != null ? new Integer(values.get("RefcontigID2")) : null);
        entry.setQryStartPos(values.get("QryStartPos") != null ? new Double(values.get("QryStartPos")) : null);
        entry.setQryEndPos(values.get("QryEndPos") != null ? new Double(values.get("QryEndPos")) : null);
        entry.setRefStartPos(values.get("RefStartPos") != null ? new Double(values.get("RefStartPos")) : null);
        entry.setRefEndPos(values.get("RefEndPos") != null ? new Double(values.get("RefEndPos")) : null);
        entry.setConfidence(values.get("Confidence") != null ? new Double(values.get("Confidence")) : null);
        entry.setType(values.get("Type"));
        entry.setXmapID1(values.get("XmapID1") != null ? new Integer(values.get("XmapID1")) : null);
        entry.setXmapID2(values.get("XmapID2") != null ? new Integer(values.get("XmapID2")) : null);
        entry.setLinkID(values.get("LinkID") != null ? new Integer(values.get("LinkID")) : null);
        entry.setQryStartIdx(values.get("QryStartIdx") != null ? new Integer(values.get("QryStartIdx")) : null);
        entry.setQryEndIdx(values.get("QryEndIdx") != null ? new Integer(values.get("QryEndIdx")) : null);
        entry.setRefStartIdx(values.get("RefStartIdx") != null ? new Integer(values.get("RefStartIdx")) : null);
        entry.setRefEndIdx(values.get("RefEndIdx") != null ? new Integer(values.get("RefEndIdx")) : null);
        entry.setZygosity(values.get("Zygosity"));
        entry.setGenotype(values.get("Genotype") != null ? new Integer(values.get("Genotype")) : null);
        entry.setGenotypeGroup(values.get("GenotypeGroup") != null ? new Integer(values.get("GenotypeGroup")) : null);
        entry.setRawConfidence(values.get("RawConfidence") != null ? new Double(values.get("RawConfidence")) : null);
        entry.setRawConfidenceLeft(values.get("RawConfidenceLeft") != null ? new Double(values.get("RawConfidenceLeft")) : null);
        entry.setRawConfidenceRight(values.get("RawConfidenceRight") != null ? new Double(values.get("RawConfidenceRight")) : null);
        entry.setRawConfidenceCenter(values.get("RawConfidenceCenter") != null ? new Double(values.get("RawConfidenceCenter")) : null);
        entry.setSvSize(values.get("SVsize") != null ? new Double(values.get("SVsize")) : null);
        entry.setSvFreq(values.get("SVfreq") != null ? new Double(values.get("SVfreq")) : null);
        entry.setOrientation(values.get("Orientation"));
        entry.setVaf(values.get("VAF"));
        entry.setSample(values.get("Sample"));
        entry.setAlgorithm(values.get("Algorithm"));
        entry.setSize(values.get("Size") != null ? new Integer(values.get("Size")) : null);
        entry.setPresentInPercentBngControlSamples(values.get("Present_in_%_of_BNG_control_samples") != null ? new Double(values.get("Present_in_%_of_BNG_control_samples")) : null);
        entry.setPresentInPercentBngControlSamplesSameEnzyme(values.get("Present_in_%_of_BNG_control_samples_with_the_same_enzyme") != null ? new Double(values.get("Present_in_%_of_BNG_control_samples_with_the_same_enzyme")) : null);
        entry.setFailAssemblyChimericScore(values.get("Fail_assembly_chimeric_score"));
        entry.setNumOverlapDgvCalls(values.get("num_overlap_DGV_calls") != null ? new Integer(values.get("num_overlap_DGV_calls")) : null);
        entry.setOverlapGenes(values.get("OverlapGenes"));
        entry.setNearestNonOverlapGene(values.get("NearestNonOverlapGene"));
        entry.setNearestNonOverlapGeneDistance(values.get("NearestNonOverlapGeneDistance") != null ? new Double(values.get("NearestNonOverlapGeneDistance")) : null);
        entry.setPutativeGeneFusion(values.get("PutativeGeneFusion"));
        entry.setFoundInSelfMolecules(values.get("Found_in_self_molecules"));
        entry.setSelfMoleculeCount(values.get("Self_molecule_count") != null ? new Integer(values.get("Self_molecule_count")) : null);
        entry.setUcscWebLink1(values.get("UCSC_web_link1"));
        entry.setUcscWebLink2(values.get("UCSC_web_link2"));

        return entry;
    }
}
