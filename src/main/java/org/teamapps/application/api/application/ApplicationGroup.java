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
package org.teamapps.application.api.application;

import org.teamapps.icons.Icon;

public class ApplicationGroup {

	private final Icon icon;
	private final String name;
	private final String titleKey;

	public ApplicationGroup(Icon icon, String name, String titleKey) {
		this.icon = icon;
		this.name = name;
		this.titleKey = titleKey;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	public String getTitleKey() {
		return titleKey;
	}
}
