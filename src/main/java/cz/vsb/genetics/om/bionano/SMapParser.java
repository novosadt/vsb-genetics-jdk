package cz.vsb.genetics.om.bionano;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

public class SMapParser {
    public SMap parse(String file) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        SMap smap = new SMap();

        String line;
        while((line = reader.readLine()) != null) {
            if (line.startsWith("#") || StringUtils.isBlank(line))
                continue;

            SMapEntry entry = parseLine(line);

            smap.add(entry);
        }

        reader.close();

        return smap;
    }

    private SMapEntry parseLine(String line) {
        String[] values = line.split("\t");

        int l = values.length;
        int i = 1;

        SMapEntry entry = new SMapEntry(new Integer(values[0]));
        entry.setQryContigID(l < ++i ? null : new Integer(values[1]));
        entry.setRefcontigID1(l < ++i ? null : new Integer(values[2]));
        entry.setRefcontigID2(l < ++i ? null : new Integer(values[3]));
        entry.setQryStartPos(l < ++i ? null : new Float(values[4]));
        entry.setQryEndPos(l < ++i ? null : new Float(values[5]));
        entry.setRefStartPos(l < ++i ? null : new Float(values[6]));
        entry.setRefEndPos(l < ++i ? null : new Float(values[7]));
        entry.setConfidence(l < ++i ? null : new Float(values[8]));
        entry.setType(l < ++i ? null : values[9]);
        entry.setXmapID1(l < ++i ? null : new Integer(values[10]));
        entry.setXmapID2(l < ++i ? null : new Integer(values[11]));
        entry.setLinkID(l < ++i ? null : new Integer(values[12]));
        entry.setQryStartIdx(l < ++i ? null : new Integer(values[13]));
        entry.setQryEndIdx(l < ++i ? null : new Integer(values[14]));
        entry.setRefStartIdx(l < ++i ? null : new Integer(values[15]));
        entry.setRefEndIdx(l < ++i ? null : new Integer(values[16]));
        entry.setZygosity(l < ++i ? null : values[17]);
        entry.setGenotype(l < ++i ? null : new Integer(values[18]));
        entry.setGenotypeGroup(l < ++i ? null : new Integer(values[19]));
        entry.setRawConfidence(l < ++i ? null : new Float(values[20]));
        entry.setRawConfidenceLeft(l < ++i ? null : new Float(values[21]));
        entry.setRawConfidenceRight(l < ++i ? null : new Float(values[22]));
        entry.setRawConfidenceCenter(l < ++i ? null : new Float(values[23]));
        entry.setSvSize(l < ++i ? null : new Float(values[24]));
        entry.setSvFreq(l < ++i ? null : new Float(values[25]));
        entry.setOrientation(l < ++i ? null : values[26]);
        entry.setSample(l < ++i ? null : values[27]);
        entry.setAlgorithm(l < ++i ? null : values[28]);
        entry.setSize(l < ++i ? null : new Integer(values[29]));
        entry.setPresentInPercentBngControlSamples(l < ++i ? null : new Float(values[30]));
        entry.setPresentInPercentBngControlSamplesSameEnzyme(l < ++i ? null : new Float(values[31]));
        entry.setFailAssemblyChimericScore(l < ++i ? null : values[32]);
        entry.setNumOverlapDgvCalls(l < ++i ? null : new Integer(values[33]));
        entry.setOverlapGenes(l < ++i ? null : values[34]);
        entry.setNearestNonOverlapGene(l < ++i ? null : values[35]);
        entry.setNearestNonOverlapGeneDistance(l < ++i ? null : new Integer(values[36]));
        entry.setPutativeGeneFusion(l < ++i ? null : values[37]);
        entry.setFoundInSelfMolecules(l < ++i ? null : values[38]);
        entry.setSelfMoleculeCount(l < ++i ? null : new Integer(values[39]));
        entry.setUcscWebLink1(l < ++i ? null : values[40]);
        entry.setUcscWebLink2(l < ++i ? null : values[41]);

        return entry;
    }
}
