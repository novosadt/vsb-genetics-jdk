package cz.vsb.genetics.om.bionano;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

public class XMapParser {
    public XMap parse(String file) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        XMap xmap = new XMap();

        String line;
        while((line = reader.readLine()) != null) {
            if (line.startsWith("#") || StringUtils.isBlank(line))
                continue;

            XMapEntry entry = parseLine(line);

            xmap.add(entry);
        }

        reader.close();

        return xmap;
    }

    private XMapEntry parseLine(String line) {
        String[] values = line.split("\t");

        int l = values.length;
        int i = 1;

        XMapEntry entry = new XMapEntry(new Integer(values[0]));
        entry.setQryContigID(l < ++i ? null : new Integer(values[1]));
        entry.setRefContigID(l < ++i ? null : new Integer(values[2]));
        entry.setQryStartPos(l < ++i ? null : new Float(values[3]));
        entry.setQryEndPos(l < ++i ? null : new Float(values[4]));
        entry.setRefStartPos(l < ++i ? null : new Float(values[5]));
        entry.setRefEndPos(l < ++i ? null : new Float(values[6]));
        entry.setOrientation(l < ++i ? null : values[7]);
        entry.setConfidence(l < ++i ? null : new Float(values[8]));
        entry.setHitEnum(l < ++i ? null : values[9]);
        entry.setQryLen(l < ++i ? null : new Float(values[10]));
        entry.setRefLen(l < ++i ? null : new Float(values[11]));
        entry.setLabelChannel(l < ++i ? null : new Integer(values[12]));
        entry.setAlignment(l < ++i ? null : values[13]);

        return entry;
    }
}
