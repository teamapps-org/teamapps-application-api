package org.teamapps.application.ui.drilldown;


import java.util.ArrayList;
import java.util.List;

public class FacetNode<RECORD> {
	private final FacetNode<RECORD> parent;
	private final DrillDownFacet<?, RECORD> facet;
	private final Object facetRecord;
	private final List<RECORD> records;
	private final int count;
	private final List<FacetNode<RECORD>> children = new ArrayList<>();
	private int totalFacets;


	public FacetNode(DrillDownFacet<?, RECORD> facet) {
		this.parent = null;
		this.facet = facet;
		this.facetRecord = null;
		this.records = null;
		this.count = 0;
	}

	public FacetNode(FacetNode<RECORD> parent, DrillDownFacet<?, RECORD> facet, Object facetRecord, List<RECORD> records, int count) {
		this.parent = parent;
		this.facet = facet;
		this.facetRecord = facetRecord;
		this.records = records;
		this.count = count;
		this.parent.getChildren().add(this);
	}

	public FacetNode<RECORD> parent() {
		return parent;
	}

	public DrillDownFacet<?, RECORD> facet() {
		return facet;
	}

	public Object facetRecord() {
		return facetRecord;
	}

	public List<RECORD> records() {
		return records;
	}

	public int count() {
		return count;
	}

	public List<FacetNode<RECORD>> getChildren() {
		return children;
	}

	public void setTotalFacets(int totalFacets) {
		this.totalFacets = totalFacets;
	}

	public int getTotalFacets() {
		return totalFacets;
	}
}
