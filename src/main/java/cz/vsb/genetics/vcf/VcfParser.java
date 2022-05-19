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
