package cz.vsb.genetics.om.bionano;

import java.util.List;

public class XMapEntry {
    private final Integer xmapEntryID;
    private Integer qryContigID;
    private Integer refContigID;
    private Float qryStartPos;
    private Float qryEndPos;
    private Float refStartPos;
    private Float refEndPos;
    private String orientation;
    private Float confidence;
    private String hitEnum;
    private Float qryLen;
    private Float refLen;
    private Integer labelChannel;
    private String alignment;
    private List<XMapAlignmentEntry> alignments;

    public XMapEntry(Integer xmapEntryID) {
        assert xmapEntryID != null;

        this.xmapEntryID = xmapEntryID;
    }

    public Integer getXmapEntryID() {
        return xmapEntryID;
    }

    public Integer getQryContigID() {
        return qryContigID;
    }

    public void setQryContigID(Integer qryContigID) {
        this.qryContigID = qryContigID;
    }

    public Integer getRefContigID() {
        return refContigID;
    }

    public void setRefContigID(Integer refContigID) {
        this.refContigID = refContigID;
    }

    public Float getQryStartPos() {
        return qryStartPos;
    }

    public void setQryStartPos(Float qryStartPos) {
        this.qryStartPos = qryStartPos;
    }

    public Float getQryEndPos() {
        return qryEndPos;
    }

    public void setQryEndPos(Float qryEndPos) {
        this.qryEndPos = qryEndPos;
    }

    public Float getRefStartPos() {
        return refStartPos;
    }

    public void setRefStartPos(Float refStartPos) {
        this.refStartPos = refStartPos;
    }

    public Float getRefEndPos() {
        return refEndPos;
    }

    public void setRefEndPos(Float refEndPos) {
        this.refEndPos = refEndPos;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }

    public String getHitEnum() {
        return hitEnum;
    }

    public void setHitEnum(String hitEnum) {
        this.hitEnum = hitEnum;
    }

    public Float getQryLen() {
        return qryLen;
    }

    public void setQryLen(Float qryLen) {
        this.qryLen = qryLen;
    }

    public Float getRefLen() {
        return refLen;
    }

    public void setRefLen(Float refLen) {
        this.refLen = refLen;
    }

    public Integer getLabelChannel() {
        return labelChannel;
    }

    public void setLabelChannel(Integer labelChannel) {
        this.labelChannel = labelChannel;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public List<XMapAlignmentEntry> getAlignments() {
        return alignments;
    }

    public void setAlignments(List<XMapAlignmentEntry> alignments) {
        this.alignments = alignments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XMapEntry)) return false;

        XMapEntry xMapEntry = (XMapEntry) o;

        return getXmapEntryID().equals(xMapEntry.getXmapEntryID());
    }

    @Override
    public int hashCode() {
        return getXmapEntryID().hashCode();
    }

    public static class XMapAlignmentEntry {
        private final Integer refContigId;
        private final Integer qryContigId;
        private final Integer refContigSiteId;
        private final Integer qryContigSiteId;

        public XMapAlignmentEntry(Integer refContigId, Integer qryContigId,
                                  Integer refContigSiteId, Integer qryContigSiteId) {
            this.refContigId = refContigId;
            this.qryContigId = qryContigId;
            this.refContigSiteId = refContigSiteId;
            this.qryContigSiteId = qryContigSiteId;
        }

        public Integer getRefContigId() {
            return refContigId;
        }

        public Integer getQryContigId() {
            return qryContigId;
        }

        public Integer getRefContigSiteId() {
            return refContigSiteId;
        }

        public Integer getQryContigSiteId() {
            return qryContigSiteId;
        }
    }

}
