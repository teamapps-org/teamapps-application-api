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
