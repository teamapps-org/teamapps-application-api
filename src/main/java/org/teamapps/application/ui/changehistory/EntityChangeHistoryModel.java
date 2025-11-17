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
package org.teamapps.application.ui.changehistory;

import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.record.EntityBuilder;

public interface EntityChangeHistoryModel<ENTITY> {

	EntityBuilder<ENTITY> getEntityBuilder();

	ObservableValue<ENTITY> getSelectedEntity();

	static <ENTITY extends Entity<ENTITY>> EntityChangeHistoryModel<ENTITY> createModelFromEntity(ENTITY entity) {
		return new EntityChangeHistoryModel<ENTITY>() {
			@Override
			public EntityBuilder<ENTITY> getEntityBuilder() {
				return (AbstractUdbEntity<ENTITY>) entity;
			}

			@Override
			public ObservableValue<ENTITY> getSelectedEntity() {
				return TwoWayBindableValue.create(entity);
			}
		};
	}


}
