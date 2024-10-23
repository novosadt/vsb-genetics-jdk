/*
 * Copyright (C) 2021  Tomas Novosad
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


package cz.vsb.genetics.sv;

import cz.vsb.genetics.util.GeneAnnotator;

import java.util.List;
import java.util.Set;

public interface SvResultParser {
    void parseResultFile(String file, String delim) throws Exception;

    void printStructuralVariantStats();

    void setRemoveDuplicateVariants(boolean value);

    Set<StructuralVariant> getTranslocations();

    Set<StructuralVariant> getDuplications();

    Set<StructuralVariant> getInversions();

    Set<StructuralVariant> getDeletions();

    Set<StructuralVariant> getInsertions();

    Set<StructuralVariant> getCopyNumberVariations();

    Set<StructuralVariant> getUnknown();

    List<StructuralVariant> getVariants();

    String getName();

    void setInfoTags(String[] infoTags);

    String[] getInfoTags();

    void setGeneAnnotator(GeneAnnotator geneAnnotator);
}
