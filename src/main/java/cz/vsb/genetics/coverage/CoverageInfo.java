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


package cz.vsb.genetics.coverage;

public class CoverageInfo {
    String name;
    int[] coverages;
    int minCoverage;
    int maxCoverage;
    int positionStart;
    int positionEnd;
    int samplingSize = 0;
    int coverageLimit = 0;
    int siteCount = 0;
    Integer color;

    public CoverageInfo(int[] coverages, int minCoverage, int maxCoverage, int positionStart, int positionEnd) {
        this.coverages = coverages;
        this.minCoverage = minCoverage;
        this.maxCoverage = maxCoverage;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
    }

    public int[] getCoverages() {
        return coverages;
    }

    public void setCoverages(int[] coverages) {
        this.coverages = coverages;
    }

    public int getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(int maxCoverage) {
        this.maxCoverage = maxCoverage;
    }

    public int getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(int minCoverage) {
        this.minCoverage = minCoverage;
    }

    public int getPositionStart() {
        return positionStart;
    }

    public void setPositionStart(int positionStart) {
        this.positionStart = positionStart;
    }

    public int getPositionEnd() {
        return positionEnd;
    }

    public void setPositionEnd(int positionEnd) {
        this.positionEnd = positionEnd;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSamplingSize() {
        return samplingSize;
    }

    public void setSamplingSize(int samplingSize) {
        this.samplingSize = samplingSize;
    }

    public int getCoverageLimit() {
        return coverageLimit;
    }

    public void setCoverageLimit(int coverageLimit) {
        this.coverageLimit = coverageLimit;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public int getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(int siteCount) {
        this.siteCount = siteCount;
    }
}
