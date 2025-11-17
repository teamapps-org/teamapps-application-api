/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
