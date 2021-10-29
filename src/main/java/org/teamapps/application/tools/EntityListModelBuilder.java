/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.tools;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class EntityListModelBuilder<ENTITY extends Entity<ENTITY>> extends RecordListModelBuilder<ENTITY> {

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<ENTITY, String> entityStringFunction) {
		super(applicationInstanceData, entityStringFunction);
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<ENTITY, String> entityStringFunction, Function<ENTITY, Long> entityDateInMillisFunction) {
		super(applicationInstanceData, entityStringFunction, entityDateInMillisFunction);
	}

}
