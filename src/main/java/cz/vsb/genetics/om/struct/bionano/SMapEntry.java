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

public class SMapEntry {
    private final Integer smapEntryID;
    private Integer qryContigID;
    private Integer refcontigID1;
    private Integer refcontigID2;
    private Double qryStartPos;
    private Double qryEndPos;
    private Double refStartPos;
    private Double refEndPos;
    private Double confidence;
    private String type;
    private Integer xmapID1;
    private Integer xmapID2;
    private Integer linkID;
    private Integer qryStartIdx;
    private Integer qryEndIdx;
    private Integer refStartIdx;
    private Integer refEndIdx;
    private String zygosity;
    private Integer genotype;
    private Integer genotypeGroup;
    private Double rawConfidence;
    private Double rawConfidenceLeft;
    private Double rawConfidenceRight;
    private Double rawConfidenceCenter;
    private Double svSize;
    private Double svFreq;
    private String orientation;
    private Double vaf;
    private String sample;
    private String algorithm;
    private Integer size;
    private Double presentInPercentBngControlSamples;
    private Double presentInPercentBngControlSamplesSameEnzyme;
    private String failAssemblyChimericScore;
    private Integer numOverlapDgvCalls;
    private String overlapGenes;
    private String nearestNonOverlapGene;
    private Double nearestNonOverlapGeneDistance;
    private String putativeGeneFusion;
    private String foundInSelfMolecules;
    private Integer selfMoleculeCount;
    private String ucscWebLink1;
    private String ucscWebLink2;

    public SMapEntry(Integer smapEntryID) {
        assert smapEntryID != null;

        this.smapEntryID = smapEntryID;
    }

    public Integer getSmapEntryID() {
        return smapEntryID;
    }

    public Integer getQryContigID() {
        return qryContigID;
    }

    public void setQryContigID(Integer qryContigID) {
        this.qryContigID = qryContigID;
    }

    public Integer getRefcontigID1() {
        return refcontigID1;
    }

    public void setRefcontigID1(Integer refcontigID1) {
        this.refcontigID1 = refcontigID1;
    }

    public Integer getRefcontigID2() {
        return refcontigID2;
    }

    public void setRefcontigID2(Integer refcontigID2) {
        this.refcontigID2 = refcontigID2;
    }

    public Double getQryStartPos() {
        return qryStartPos;
    }

    public void setQryStartPos(Double qryStartPos) {
        this.qryStartPos = qryStartPos;
    }

    public Double getQryEndPos() {
        return qryEndPos;
    }

    public void setQryEndPos(Double qryEndPos) {
        this.qryEndPos = qryEndPos;
    }

    public Double getRefStartPos() {
        return refStartPos;
    }

    public void setRefStartPos(Double refStartPos) {
        this.refStartPos = refStartPos;
    }

    public Double getRefEndPos() {
        return refEndPos;
    }

    public void setRefEndPos(Double refEndPos) {
        this.refEndPos = refEndPos;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getXmapID1() {
        return xmapID1;
    }

    public void setXmapID1(Integer xmapID1) {
        this.xmapID1 = xmapID1;
    }

    public Integer getXmapID2() {
        return xmapID2;
    }

    public void setXmapID2(Integer xmapID2) {
        this.xmapID2 = xmapID2;
    }

    public Integer getLinkID() {
        return linkID;
    }

    public void setLinkID(Integer linkID) {
        this.linkID = linkID;
    }

    public Integer getQryStartIdx() {
        return qryStartIdx;
    }

    public void setQryStartIdx(Integer qryStartIdx) {
        this.qryStartIdx = qryStartIdx;
    }

    public Integer getQryEndIdx() {
        return qryEndIdx;
    }

    public void setQryEndIdx(Integer qryEndIdx) {
        this.qryEndIdx = qryEndIdx;
    }

    public Integer getRefStartIdx() {
        return refStartIdx;
    }

    public void setRefStartIdx(Integer refStartIdx) {
        this.refStartIdx = refStartIdx;
    }

    public Integer getRefEndIdx() {
        return refEndIdx;
    }

    public void setRefEndIdx(Integer refEndIdx) {
        this.refEndIdx = refEndIdx;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public Integer getGenotype() {
        return genotype;
    }

    public void setGenotype(Integer genotype) {
        this.genotype = genotype;
    }

    public Integer getGenotypeGroup() {
        return genotypeGroup;
    }

    public void setGenotypeGroup(Integer genotypeGroup) {
        this.genotypeGroup = genotypeGroup;
    }

    public Double getRawConfidence() {
        return rawConfidence;
    }

    public void setRawConfidence(Double rawConfidence) {
        this.rawConfidence = rawConfidence;
    }

    public Double getRawConfidenceLeft() {
        return rawConfidenceLeft;
    }

    public void setRawConfidenceLeft(Double rawConfidenceLeft) {
        this.rawConfidenceLeft = rawConfidenceLeft;
    }

    public Double getRawConfidenceRight() {
        return rawConfidenceRight;
    }

    public void setRawConfidenceRight(Double rawConfidenceRight) {
        this.rawConfidenceRight = rawConfidenceRight;
    }

    public Double getRawConfidenceCenter() {
        return rawConfidenceCenter;
    }

    public void setRawConfidenceCenter(Double rawConfidenceCenter) {
        this.rawConfidenceCenter = rawConfidenceCenter;
    }

    public Double getSvSize() {
        return svSize;
    }

    public void setSvSize(Double svSize) {
        this.svSize = svSize;
    }

    public Double getSvFreq() {
        return svFreq;
    }

    public void setSvFreq(Double svFreq) {
        this.svFreq = svFreq;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Double getVaf() {
        return vaf;
    }

    public void setVaf(Double vaf) {
        this.vaf = vaf;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPresentInPercentBngControlSamples() {
        return presentInPercentBngControlSamples;
    }

    public void setPresentInPercentBngControlSamples(Double presentInPercentBngControlSamples) {
        this.presentInPercentBngControlSamples = presentInPercentBngControlSamples;
    }

    public Double getPresentInPercentBngControlSamplesSameEnzyme() {
        return presentInPercentBngControlSamplesSameEnzyme;
    }

    public void setPresentInPercentBngControlSamplesSameEnzyme(Double presentInPercentBngControlSamplesSameEnzyme) {
        this.presentInPercentBngControlSamplesSameEnzyme = presentInPercentBngControlSamplesSameEnzyme;
    }

    public String getFailAssemblyChimericScore() {
        return failAssemblyChimericScore;
    }

    public void setFailAssemblyChimericScore(String failAssemblyChimericScore) {
        this.failAssemblyChimericScore = failAssemblyChimericScore;
    }

    public Integer getNumOverlapDgvCalls() {
        return numOverlapDgvCalls;
    }

    public void setNumOverlapDgvCalls(Integer numOverlapDgvCalls) {
        this.numOverlapDgvCalls = numOverlapDgvCalls;
    }

    public String getOverlapGenes() {
        return overlapGenes;
    }

    public void setOverlapGenes(String overlapGenes) {
        this.overlapGenes = overlapGenes;
    }

    public String getNearestNonOverlapGene() {
        return nearestNonOverlapGene;
    }

    public void setNearestNonOverlapGene(String nearestNonOverlapGene) {
        this.nearestNonOverlapGene = nearestNonOverlapGene;
    }

    public Double getNearestNonOverlapGeneDistance() {
        return nearestNonOverlapGeneDistance;
    }

    public void setNearestNonOverlapGeneDistance(Double nearestNonOverlapGeneDistance) {
        this.nearestNonOverlapGeneDistance = nearestNonOverlapGeneDistance;
    }

    public String getPutativeGeneFusion() {
        return putativeGeneFusion;
    }

    public void setPutativeGeneFusion(String putativeGeneFusion) {
        this.putativeGeneFusion = putativeGeneFusion;
    }

    public String getFoundInSelfMolecules() {
        return foundInSelfMolecules;
    }

    public void setFoundInSelfMolecules(String foundInSelfMolecules) {
        this.foundInSelfMolecules = foundInSelfMolecules;
    }

    public Integer getSelfMoleculeCount() {
        return selfMoleculeCount;
    }

    public void setSelfMoleculeCount(Integer selfMoleculeCount) {
        this.selfMoleculeCount = selfMoleculeCount;
    }

    public String getUcscWebLink1() {
        return ucscWebLink1;
    }

    public void setUcscWebLink1(String ucscWebLink1) {
        this.ucscWebLink1 = ucscWebLink1;
    }

    public String getUcscWebLink2() {
        return ucscWebLink2;
    }

    public void setUcscWebLink2(String ucscWebLink2) {
        this.ucscWebLink2 = ucscWebLink2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMapEntry)) return false;

        SMapEntry sMapEntry = (SMapEntry) o;

        return getSmapEntryID().equals(sMapEntry.getSmapEntryID());
    }

    @Override
    public int hashCode() {
        return getSmapEntryID().hashCode();
    }
}
