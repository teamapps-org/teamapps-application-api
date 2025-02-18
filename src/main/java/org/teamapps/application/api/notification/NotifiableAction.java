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
package org.teamapps.application.api.notification;

public enum NotifiableAction {

	NEW_ENTITY(1),
	UPDATED_ENTITY(2),
	DELETED_ENTITY(3),
	RESTORED_ENTITY(4),
	MOVE_TO_ORG_UNIT(5),
	ENTITY_STATUS_CHANGED(6),

	;
	private final int actionId;

	NotifiableAction(int actionId) {
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public static NotifiableAction fromActionId(int actionId) {
		for (NotifiableAction action : values()) {
			if (action.getActionId() == actionId) {
				return action;
			}
		}
		return null;
	}
}
