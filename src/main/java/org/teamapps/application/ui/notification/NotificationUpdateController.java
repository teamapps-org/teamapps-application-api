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
package org.teamapps.application.ui.notification;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface NotificationUpdateController<ENTITY> {

	static <ENTITY extends Entity<ENTITY>> NotificationUpdateController<ENTITY> createNotificationUpdateController(LifecycleEntityModel<ENTITY> lifecycleEntityModel, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		return new NotificationUpdateControllerImpl<>(lifecycleEntityModel, unitByEntityFunction, applicationInstanceData);
	}

	void registerNotifyOnCreation();

	void registerNotifyOnCreation(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnUpdate();

	void registerNotifyOnUpdate(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnDeletion();

	void registerNotifyOnDeletion(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnRestore();

	void registerNotifyOnRestore(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnOrgMove();

	void registerNotifyOnOrgMove(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction);

	void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction, Function<ENTITY, Integer> detailIdFunction);

}
