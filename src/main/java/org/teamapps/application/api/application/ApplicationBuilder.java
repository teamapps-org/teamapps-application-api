/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
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
package org.teamapps.application.api.application;

import org.teamapps.application.api.application.perspective.PerspectiveBuilder;
import org.teamapps.application.api.application.perspective.PerspectiveData;
import org.teamapps.ux.application.ResponsiveApplication;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ApplicationBuilder extends BaseApplicationBuilder {

	List<PerspectiveBuilder> getPerspectiveBuilders();

	default List<String> getRequiredPerspectives() {
		return Collections.emptyList();
	}

	default Map<String, PerspectiveData> getRequiredPerspectivesData() {
		return null;
	}


	default String getOrganizationField() {
		return null;
	}

	@Override
	default void build(ResponsiveApplication application, ApplicationInstanceData applicationInstanceData) {
	}
}
