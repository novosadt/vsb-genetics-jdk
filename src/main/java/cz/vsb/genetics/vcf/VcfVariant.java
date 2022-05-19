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
