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


package cz.vsb.genetics.ngs.metrics;

import htsjdk.samtools.SAMRecord;

import java.util.List;

public class ReadCountInfo {
    public List<SAMRecord> splitNormal;
    public List<SAMRecord> splitNormalSupporting;
    public List<SAMRecord> anomalous;
    public int splitNormalBasesOverlap = 0;
    public int splitNormalSupportingBasesOverlap = 0;
}
