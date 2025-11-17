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

import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class DrillDownFacet<FACET_RECORD, MODEL_RECORD> {

	private final Icon icon;
	private final String title;
	private final BiFunction<FACET_RECORD, Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> filterHandler;
	private final Function<MODEL_RECORD, FACET_RECORD> grouperFunction;
	private final Function<MODEL_RECORD, Stream<FACET_RECORD>> flatMapFunction;
	private final PropertyProvider<FACET_RECORD> facetPropertyProvider;
	private final Template template;

	private Function<FACET_RECORD, String> facetRecordToStringFunction;
	private String facetRecordToStringPropertyName = BaseTemplate.PROPERTY_CAPTION;
	private int maxFacetRecords = 50;
	private int minGroupEntries = 1;
	private Function<Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> facetFilter = recordStream -> recordStream;

	public DrillDownFacet(Icon icon, String title, BiFunction<FACET_RECORD, Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> filterHandler, Function<MODEL_RECORD, FACET_RECORD> grouperFunction, Function<MODEL_RECORD, Stream<FACET_RECORD>> flatMapFunction, PropertyProvider<FACET_RECORD> facetPropertyProvider, Template template) {
		this.icon = icon;
		this.title = title;
		this.filterHandler = filterHandler;
		this.grouperFunction = grouperFunction;
		this.flatMapFunction = flatMapFunction;
		this.facetPropertyProvider = facetPropertyProvider;
		this.template = template;
	}

	public DrillDownFacet<FACET_RECORD, MODEL_RECORD> maxFacetRecords(int maxFacetRecords) {
		this.maxFacetRecords = maxFacetRecords;
		return this;
	}

	public DrillDownFacet<FACET_RECORD, MODEL_RECORD> facetFilter(Function<Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> facetFilter) {
		this.facetFilter = facetFilter;
		return this;
	}

	public DrillDownFacet<FACET_RECORD, MODEL_RECORD> facetRecordToStringFunction(Function<FACET_RECORD, String> facetRecordToStringFunction) {
		this.facetRecordToStringFunction = facetRecordToStringFunction;
		return this;
	}

	public DrillDownFacet<FACET_RECORD, MODEL_RECORD> facetRecordToStringPropertyName(String facetRecordToStringPropertyName) {
		this.facetRecordToStringPropertyName = facetRecordToStringPropertyName;
		return this;
	}

	public int getMaxFacetRecords() {
		return maxFacetRecords;
	}

	public Function<Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> getFacetFilter() {
		return facetFilter;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public boolean queryFacet(Object facetRecord, String queryValue) {
		FACET_RECORD record = (FACET_RECORD) facetRecord;
		if (queryValue == null) {
			return true;
		} else if (getFacetRecordToStringFunction() != null) {
			String value = getFacetRecordToStringFunction().apply(record);
			return value != null && value.toLowerCase().contains(queryValue);
		} else {
			String value = (String) getFacetPropertyProvider().getValues(record, Collections.singleton(getFacetRecordToStringPropertyName())).get(getFacetRecordToStringPropertyName());
			return value != null && value.toLowerCase().contains(queryValue);
		}
	}

	public Stream<MODEL_RECORD> filter(Object facetRecord, Stream<MODEL_RECORD> recordStream) {
		return filterHandler.apply((FACET_RECORD) facetRecord, recordStream);
	}

	public BiFunction<FACET_RECORD, Stream<MODEL_RECORD>, Stream<MODEL_RECORD>> getFilterHandler() {
		return filterHandler;
	}

	public Function<MODEL_RECORD, FACET_RECORD> getGrouperFunction() {
		return grouperFunction;
	}

	public Function<MODEL_RECORD, Stream<FACET_RECORD>> getFlatMapFunction() {
		return flatMapFunction;
	}

	public Function<FACET_RECORD, String> getFacetRecordToStringFunction() {
		return facetRecordToStringFunction;
	}

	public String getFacetRecordToStringPropertyName() {
		return facetRecordToStringPropertyName;
	}

	public PropertyProvider<FACET_RECORD> getFacetPropertyProvider() {
		return facetPropertyProvider;
	}

	public Template getTemplate() {
		return template;
	}

	public int getMinGroupEntries() {
		return minGroupEntries;
	}

	public DrillDownFacet<FACET_RECORD, MODEL_RECORD> setMinGroupEntries(int minGroupEntries) {
		this.minGroupEntries = minGroupEntries;
		return this;
	}
}
