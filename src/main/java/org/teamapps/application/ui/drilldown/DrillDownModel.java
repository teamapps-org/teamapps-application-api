package org.teamapps.application.ui.drilldown;

import org.teamapps.databinding.ObservableValue;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface DrillDownModel<ENTITY> {

	ObservableValue<List<ENTITY>> getFacetEntities();

	void handleFacetSelection(List<ENTITY> facetSelection);

	void handleFacetFilter(Function<Stream<ENTITY>, Stream<ENTITY>> facetFilterFunction);

}
