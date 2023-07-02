/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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
package org.teamapps.application.api.application.theme;

public class ApplicationThemeImpl implements ApplicationTheme {

	private boolean darkThemePreferred;
	private CustomApplicationTheme darkTheme;
	private CustomApplicationTheme brightTheme;

	public ApplicationThemeImpl() {
	}

	public ApplicationThemeImpl(boolean darkThemePreferred, CustomApplicationTheme darkTheme, CustomApplicationTheme brightTheme) {
		this.darkThemePreferred = darkThemePreferred;
		this.darkTheme = darkTheme;
		this.brightTheme = brightTheme;
	}

	@Override
	public boolean isDarkThemePreferred() {
		return darkThemePreferred;
	}

	@Override
	public CustomApplicationTheme getDarkTheme() {
		return darkTheme;
	}

	@Override
	public CustomApplicationTheme getBrightTheme() {
		return brightTheme;
	}

	public void setDarkThemePreferred(boolean darkThemePreferred) {
		this.darkThemePreferred = darkThemePreferred;
	}

	public void setDarkTheme(CustomApplicationTheme darkTheme) {
		this.darkTheme = darkTheme;
	}

	public void setBrightTheme(CustomApplicationTheme brightTheme) {
		this.brightTheme = brightTheme;
	}
}
