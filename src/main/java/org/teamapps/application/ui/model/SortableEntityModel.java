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
