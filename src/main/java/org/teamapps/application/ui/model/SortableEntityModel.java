package org.teamapps.application.ui.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.function.Function;

public interface SortableEntityModel<ENTITY> extends BaseEntityModel<ENTITY> {

	void handleReverseSort(boolean reverseSort);

	void handleSortLastModifiedFirst(boolean sortLastModified);

	void handleSortRequest(String fieldName,  boolean ascending, String... sortPath);

	void addSorter(String fieldName, Comparator<ENTITY> comparator);

	default void addStringSorter(String fieldName, Function<ENTITY, String> entityStringFunction) {
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
		Comparator<ENTITY> comparator = Comparator.comparing(entityStringFunction, Comparator.nullsFirst(collator));
		addSorter(fieldName, comparator);
	}

	default void addIntegerSorter(String fieldName, Function<ENTITY, Integer> entityIntegerFunction) {
		Comparator<ENTITY> comparator = Comparator.comparing(entityIntegerFunction, Comparator.nullsFirst(Comparator.naturalOrder()));
		addSorter(fieldName, comparator);
	}

	default void addLongSorter(String fieldName, Function<ENTITY, Long> entityLongFunction) {
		Comparator<ENTITY> comparator = Comparator.comparing(entityLongFunction, Comparator.nullsFirst(Comparator.naturalOrder()));
		addSorter(fieldName, comparator);
	}

	default void addBooleanSorter(String fieldName, Function<ENTITY, Boolean> entityBooleanFunction) {
		Comparator<ENTITY> comparator = Comparator.comparing(entityBooleanFunction, Comparator.nullsFirst(Comparator.naturalOrder()));
		addSorter(fieldName, comparator);
	}

}
