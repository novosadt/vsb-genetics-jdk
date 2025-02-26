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

public class CMapEntry {
    private final Integer cmapId;
    private Float contigLength;
    private Integer numSites;
    private final Integer siteId;
    private Integer labelChannel;
    private Float position;
    private Float stdDev;
    private Float coverage;
    private Float occurrence;
    private Float chimQuality;
    private Float segDupL;
    private Float segDupR;
    private Float fragileL;
    private Float fragileR;
    private Float outlierFrac;
    private Float chimNorm;

    public CMapEntry(Integer cmapId, Integer siteId) {
        assert (cmapId !=null && siteId != null);

        this.cmapId = cmapId;
        this.siteId = siteId;
    }

    public Integer getCmapId() {
        return cmapId;
    }

    public Float getContigLength() {
        return contigLength;
    }

    public void setContigLength(Float contigLength) {
        this.contigLength = contigLength;
    }

    public Integer getNumSites() {
        return numSites;
    }

    public void setNumSites(Integer numSites) {
        this.numSites = numSites;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getLabelChannel() {
        return labelChannel;
    }

    public void setLabelChannel(Integer labelChannel) {
        this.labelChannel = labelChannel;
    }

    public Float getPosition() {
        return position;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public Float getStdDev() {
        return stdDev;
    }

    public void setStdDev(Float stdDev) {
        this.stdDev = stdDev;
    }

    public Float getCoverage() {
        return coverage;
    }

    public void setCoverage(Float coverage) {
        this.coverage = coverage;
    }

    public Float getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Float occurrence) {
        this.occurrence = occurrence;
    }

    public Float getChimQuality() {
        return chimQuality;
    }

    public void setChimQuality(Float chimQuality) {
        this.chimQuality = chimQuality;
    }

    public Float getSegDupL() {
        return segDupL;
    }

    public void setSegDupL(Float segDupL) {
        this.segDupL = segDupL;
    }

    public Float getSegDupR() {
        return segDupR;
    }

    public void setSegDupR(Float segDupR) {
        this.segDupR = segDupR;
    }

    public Float getFragileL() {
        return fragileL;
    }

    public void setFragileL(Float fragileL) {
        this.fragileL = fragileL;
    }

    public Float getFragileR() {
        return fragileR;
    }

    public void setFragileR(Float fragileR) {
        this.fragileR = fragileR;
    }

    public Float getOutlierFrac() {
        return outlierFrac;
    }

    public void setOutlierFrac(Float outlierFrac) {
        this.outlierFrac = outlierFrac;
    }

    public Float getChimNorm() {
        return chimNorm;
    }

    public void setChimNorm(Float chimNorm) {
        this.chimNorm = chimNorm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CMapEntry)) return false;

        CMapEntry cMapEntry = (CMapEntry) o;

        if (!getCmapId().equals(cMapEntry.getCmapId())) return false;
        return getSiteId().equals(cMapEntry.getSiteId());
    }

    @Override
    public int hashCode() {
        int result = getCmapId().hashCode();
        result = 31 * result + getSiteId().hashCode();
        return result;
    }
}
