package org.teamapps.application.ui.model;

import org.teamapps.application.ui.drilldown.DrillDownModel;
import org.teamapps.application.ui.timefilter.TimeGraphModel;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public interface FilterableEntityModel<ENTITY extends Entity<ENTITY>> extends BaseEntityModel<ENTITY>, DrillDownModel<ENTITY>, TimeGraphModel<ENTITY> {

	void setCustomFulltextFilter(BiFunction<String, Stream<ENTITY>, Stream<ENTITY>> customFulltextFilter);

	void setCustomFullTextFilter(BiConsumer<String, Query<ENTITY>> customFullTextFilterHandler);

	void addCustomEntityFilter(String filterName, Function<Stream<ENTITY>, Stream<ENTITY>> customEntityFilter);

	void removeCustomEntityFilter(String filterName);
}
