package cz.vsb.genetics.ngs.bam;

import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;

public class BamUtils {
    public static SamReader getSamReader(String bamFile, String indexFile) {
        final SamReaderFactory factory =
                SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS)
                        .validationStringency(ValidationStringency.LENIENT);

        final SamInputResource resource = SamInputResource.of(new File(bamFile)).index(new File(indexFile));

        return factory.open(resource);
    }
}
