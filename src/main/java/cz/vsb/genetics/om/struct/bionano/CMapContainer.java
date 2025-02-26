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

import java.util.HashMap;
import java.util.Map;

public class CMapContainer {
    private final Map<Integer, CMap> cmaps = new HashMap<>();

    public void add(CMap cmap) {
        cmaps.put(cmap.getId(), cmap);
    }

    public CMap get(Integer cmapId) {
        return cmaps.get(cmapId);
    }

}
