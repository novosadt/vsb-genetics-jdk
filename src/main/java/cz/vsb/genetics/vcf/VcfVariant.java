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


package cz.vsb.genetics.vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

public class VcfVariant {
	private final String id;
	private final VCFHeader vcfHeader;
	private final VariantContext variantContext;

	public VcfVariant(VCFHeader vcfHeader, VariantContext variantContext) {
		this.vcfHeader = vcfHeader;
		this.variantContext = variantContext;
		this.id = variantContext.getID();
	}

	public String getId() {
		return id;
	}

	public String getContig() {
		return variantContext.getContig();
	}

	public int getPositionStart() {
		return variantContext.getStart();
	}

	public int getPositionEnd() {
		return variantContext.getEnd();
	}

	public Object getInfo(String key) {
		return variantContext.getAttribute(key);
	}
}
