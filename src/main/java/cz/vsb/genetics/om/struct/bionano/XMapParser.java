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


package cz.vsb.genetics.om.struct.bionano;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        XMapEntry entry = new XMapEntry(new Integer(values[0]));
        entry.setQryContigID(new Integer(values[1]));
        entry.setRefContigID(new Integer(values[2]));
        entry.setQryStartPos(new Float(values[3]));
        entry.setQryEndPos(new Float(values[4]));
        entry.setRefStartPos(new Float(values[5]));
        entry.setRefEndPos(new Float(values[6]));
        entry.setOrientation(values[7]);
        entry.setConfidence(new Float(values[8]));
        entry.setHitEnum(values[9]);
        entry.setQryLen(new Float(values[10]));
        entry.setRefLen(new Float(values[11]));
        entry.setLabelChannel(new Integer(values[12]));
        entry.setAlignment(values[13]);
        entry.setAlignments(getAlignments(entry));

        return entry;
    }

    private List<XMapEntry.XMapAlignmentEntry> getAlignments(XMapEntry entry) {

        String regex = "\\((\\d+),(\\d+)\\)";

        String alignment = entry.getAlignment();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(alignment);

        List<XMapEntry.XMapAlignmentEntry> alignmentEntries = new ArrayList<>();

        while(matcher.find()) {
            alignmentEntries.add(
                    new XMapEntry.XMapAlignmentEntry(entry.getRefContigID(), entry.getQryContigID(),
                            new Integer(matcher.group(1)), new Integer(matcher.group(2))));

        }

        return alignmentEntries;
    }
}
