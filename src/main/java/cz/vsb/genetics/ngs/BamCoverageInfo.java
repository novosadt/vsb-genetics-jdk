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

package cz.vsb.genetics.ngs;

import cz.vsb.genetics.common.Chromosome;
import htsjdk.samtools.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BamCoverageInfo {
    private final SamReader samReader;

    public BamCoverageInfo(String bamFile, String indexFile) throws Exception {
        final SamReaderFactory factory =
                SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);

        final SamInputResource resource = SamInputResource.of(new File(bamFile)).index(new File(indexFile));

        samReader = factory.open(resource);
    }

    public void close() throws Exception {
        samReader.close();
    }

    public long getCoverage(Chromosome chromosome, int position) {
        SAMRecordIterator it = samReader.queryOverlapping(chromosome.toString(), position, position);

        long count = it.hasNext() ? it.stream().count() : 0;

        it.close();

        return count;
    }

    public List<Long> getCoverage(Chromosome chromosome, int start, int end) {
        List<Long> coverages = new ArrayList<>();

        for (int pos = start; pos < end; pos++)
            coverages.add(getCoverage(chromosome, pos));

        return coverages;
    }
}
