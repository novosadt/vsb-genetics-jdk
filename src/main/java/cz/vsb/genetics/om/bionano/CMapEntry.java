package cz.vsb.genetics.om.bionano;

public class CMapEntry {
    Integer cmapId;
    Float contigLength;
    Integer numSites;
    Integer siteId;
    Integer labelChannel;
    Float position;
    Float stdDev;
    Float coverage;
    Float occurrence;
    Float chimQuality;
    Float segDupL;
    Float segDupR;
    Float fragileL;
    Float fragileR;
    Float outlierFrac;
    Float chimNorm;

    public Integer getCmapId() {
        return cmapId;
    }

    public void setCmapId(Integer cmapId) {
        this.cmapId = cmapId;
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

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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
}
