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

public class ReadInsertSizeInfo {
    int minInsertSize = Integer.MAX_VALUE;
    int maxInsertSize = Integer.MIN_VALUE;
    double meanInsertSize = 0.0;
    double insertSizeStd = 0.0;

    public int getMinInsertSize() {
        return minInsertSize;
    }

    public int getMaxInsertSize() {
        return maxInsertSize;
    }

    public double getMeanInsertSize() {
        return meanInsertSize;
    }

    public double getInsertSizeStd() {
        return insertSizeStd;
    }
}
