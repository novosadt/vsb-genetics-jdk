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

package cz.vsb.genetics.annotation;

import cz.vsb.genetics.common.ChromosomeRegion;
import cz.vsb.genetics.ngs.struct.RegionAnnotation;
import cz.vsb.genetics.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class RegionAnnotator {
    private static final Logger log = LoggerFactory.getLogger(RegionAnnotator.class);

    private final Map<ChromosomeRegion, RegionAnnotation> annotations = new LinkedHashMap<>();
    private final Map<ChromosomeRegion, List<RegionAnnotation>> regionAnnotations = new LinkedHashMap<>();
    private String[] headers;

    public void loadAnnotations(String annotationFile, char separator) throws IOException {
        if (StringUtils.isBlank(annotationFile) || !new File(annotationFile).exists()) {
            Util.exitError("Annotation file not found: " + annotationFile, log);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(annotationFile))) {
            String line = reader.readLine();

            if (StringUtils.isBlank(line))
                Util.exitError("Annotation file - missing header row", log);

            String delimiter = String.valueOf(separator);

            headers = line.split(delimiter);

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter);

                if (values.length < 1) {
                    log.error("Invalid line: " + line);
                    continue;
                }

                ChromosomeRegion region = ChromosomeRegion.valueOf(values[0]);

                if (region == null) {
                    log.error("Invalid region: " + line);
                    continue;
                }

                if (headers.length != values.length) {
                    Util.exitError("Annotation file - Number of annotations differs from headers. Line: " + line, log);
                }

                RegionAnnotation annotation = new RegionAnnotation();
                annotation.setRegion(region);
                annotation.setAnnotations(Arrays.asList(Arrays.copyOfRange(values, 1, values.length)));

                annotations.put(region, annotation);
            }

            headers = Arrays.copyOfRange(headers, 1, headers.length);
        }

    }

    public void findRegionAnnotations(List<ChromosomeRegion> regions) {
        regionAnnotations.clear();

        for (ChromosomeRegion region : regions) {
            List<RegionAnnotation> regionAnnotation = new ArrayList<>();
            for (ChromosomeRegion annotationRegion : annotations.keySet()) {
                if (region.intersection(annotationRegion) > 0.0)
                    regionAnnotation.add(annotations.get(annotationRegion));
            }

            regionAnnotations.put(region, regionAnnotation);
        }
    }

    public void annotate(List<ChromosomeRegion> regions, String outputFile, char separator) throws Exception {
        try (FileWriter fileWriter = new FileWriter(outputFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.append("region" + separator);
            writer.append(StringUtils.join(Arrays.asList(headers), separator));
            writer.newLine();

            for (ChromosomeRegion region : regions) {
                writer.append(region.toString() + separator);
                writer.append(regionAnnotationsToString(region, separator));
                writer.newLine();
            }
        }
    }

    public List<RegionAnnotation> getRegionAnnotations(ChromosomeRegion region) {
        List<RegionAnnotation> annotations = regionAnnotations.get(region);

        return annotations == null ? Collections.emptyList() : annotations;
    }

    public String[] getAnnotationHeaders() {
        return headers;
    }

    public String regionAnnotationsToString(ChromosomeRegion region, char separator) {
        List<RegionAnnotation> annotations = getRegionAnnotations(region);

        List<String> annotationItems = new ArrayList<>();

        for (int i = 0; i < headers.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (RegionAnnotation regionAnnotation : annotations)
                sb.append(regionAnnotation.getAnnotations().get(i) + "|");

            annotationItems.add(StringUtils.chop(sb.toString()));
        }

        return StringUtils.join(annotationItems, separator);
    }
}
