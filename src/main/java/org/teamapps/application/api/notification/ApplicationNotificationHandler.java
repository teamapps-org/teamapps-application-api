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
package org.teamapps.application.api.notification;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

public interface ApplicationNotificationHandler {

	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, NotifiableAction action, ApplicationInstanceData applicationInstanceData);


	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, int actionId, ApplicationInstanceData applicationInstanceData);


	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, int actionId, int detailId, ApplicationInstanceData applicationInstanceData);

	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, OrganizationUnitView moveToUnit, int actionId, int detailId, ApplicationInstanceData applicationInstanceData);
}
