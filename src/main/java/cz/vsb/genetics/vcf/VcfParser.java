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


package cz.vsb.genetics.vcf;

import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VcfParser {
    public List<VcfVariant> parse(String vcfFile) throws IOException {
        List<VcfVariant> vcfVariants = new ArrayList<>();

        try (final AbstractFeatureReader<VariantContext, LineIterator> reader =
                     AbstractFeatureReader.getFeatureReader(vcfFile, new VCFCodec(), false))
        {

            VCFHeader vcfHeader = (VCFHeader)reader.getHeader();

            for (final VariantContext variantContext : reader.iterator()) {
                VcfVariant vcfVariant = new VcfVariant(vcfHeader, variantContext);

                vcfVariants.add(vcfVariant);
            }
        }

        return vcfVariants;
    }
}
