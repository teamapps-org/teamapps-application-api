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

import org.teamapps.application.api.notification.NotifiableAction;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class NotificationUpdateData<ENTITY extends Entity<ENTITY>> {

	private final NotifiableAction  notifiableAction;
	private final Function<ENTITY, Integer> detailIdFunction;

	public NotificationUpdateData(NotifiableAction notifiableAction) {
		this.notifiableAction = notifiableAction;
		this.detailIdFunction = null;
	}

	public NotificationUpdateData(NotifiableAction notifiableAction, Function<ENTITY, Integer> detailIdFunction) {
		this.notifiableAction = notifiableAction;
		this.detailIdFunction = detailIdFunction;
	}

	public int getDetailId(ENTITY entity) {
		return detailIdFunction == null ? 0 : detailIdFunction.apply(entity);
	}

	public NotifiableAction getNotifiableAction() {
		return notifiableAction;
	}

	public Function<ENTITY, Integer> getDetailIdFunction() {
		return detailIdFunction;
	}
}
