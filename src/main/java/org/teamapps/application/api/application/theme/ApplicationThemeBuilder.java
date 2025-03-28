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
package org.teamapps.application.api.application.theme;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;

public class ApplicationThemeBuilder {

	public ApplicationThemeBuilder(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public static ApplicationThemeBuilder create(ClassLoader classLoader) {
		return new ApplicationThemeBuilder(classLoader);
	}

	private final ClassLoader classLoader;
	private boolean darkThemePreferred;
	private CustomApplicationThemeImpl darkTheme = new CustomApplicationThemeImpl();
	private CustomApplicationThemeImpl brightTheme = new CustomApplicationThemeImpl();

	public ApplicationThemeBuilder darkThemePreferred() {
		darkThemePreferred = true;
		return this;
	}

	public ApplicationThemeBuilder addCss(String css) {
		return addCss(css, true, true);
	}

	public ApplicationThemeBuilder addCss(String css, boolean dark, boolean bright) {
		if (css == null || css.isBlank()) {
			return this;
		}
		if (dark) {
			darkTheme.appendCss(css);
		}
		if (bright) {
			brightTheme.appendCss(css);
		}
		return this;
	}

	public ApplicationThemeBuilder addCssFromResource(String resourceName) {
		return addCssFromResource(resourceName, true, true);
	}

	public ApplicationThemeBuilder addCssFromResource(String resourceName, boolean dark, boolean bright) {
		String css = readStringResource(resourceName);
		return addCss(css, dark, bright);
	}

	public ApplicationThemeBuilder addIconStyle(Class<?> iconClass, Object style) {
		return addIconStyle(iconClass, style, true, true);
	}

	public ApplicationThemeBuilder addIconStyle(Class<?> iconClass, Object style, boolean dark, boolean bright) {
		if (style == null || iconClass == null) {
			return this;
		}
		if (dark) {
			darkTheme.addIconStyle(iconClass, style);
		}
		if (bright) {
			brightTheme.addIconStyle(iconClass, style);
		}
		return this;
	}

	public ApplicationThemeBuilder setBackgroundImage(byte[] bytes) {
		return setBackgroundImage(bytes, true, true);
	}

	public ApplicationThemeBuilder setBackgroundImage(byte[] bytes, boolean dark, boolean bright) {
		if (dark) {
			darkTheme.setBackgroundImage(bytes);
		}
		if (bright) {
			brightTheme.setBackgroundImage(bytes);
		}
		return this;
	}

	public ApplicationThemeBuilder setBackgroundImage(String resourceName) {
		return setBackgroundImage(resourceName, true, true);
	}

	public ApplicationThemeBuilder setBackgroundImage(String resourceName, boolean dark, boolean bright) {
		byte[] bytes = readByteArrayResource(resourceName);
		return setBackgroundImage(bytes, dark, bright);
	}


	public String readStringResource(String resourceName) {
		try {
			return Resources.toString(classLoader.getResource(resourceName), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] readByteArrayResource(String resourceName) {
		try {

			return Resources.toByteArray(classLoader.getResource(resourceName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationTheme build() {
		return new ApplicationThemeImpl(darkThemePreferred, darkTheme, brightTheme);
	}

}
