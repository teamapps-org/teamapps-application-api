package org.teamapps.application.ui.timefilter;

import org.teamapps.databinding.ObservableValue;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface TimeGraphModel<ENTITY> {

	EntityBuilder<ENTITY> getEntityBuilder();

	ObservableValue<List<ENTITY>> getTimeGraphEntities();

	ObservableValue<Boolean> getShowDeletedEntities();

	void handleTimeFilter(Function<Stream<ENTITY>, Stream<ENTITY>> timeFilterFunction);
}
