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

import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.Icon;

public class ValidationMessage {

	private final Icon icon;
	private final String message;

	public ValidationMessage(String message) {
		this.icon = ApplicationIcons.ERROR;
		this.message = message;
	}

	public ValidationMessage(Icon icon, String message) {
		this.icon = icon;
		this.message = message;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getMessage() {
		return message;
	}
}
