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
package org.teamapps.application.api.application.theme;

import java.util.HashMap;
import java.util.Map;

public class CustomApplicationThemeImpl implements CustomApplicationTheme {

	private Map<Class<?>, Object> iconStyleMap = new HashMap<>();
	private byte[] backgroundImage;
	private String css;

	public CustomApplicationThemeImpl() {
	}

	public CustomApplicationThemeImpl(Map<Class<?>, Object> iconStyleMap, byte[] backgroundImage, String css) {
		this.iconStyleMap = iconStyleMap;
		this.backgroundImage = backgroundImage;
		this.css = css;
	}

	@Override
	public Map<Class<?>, Object> getIconStylesForIconClass() {
		return iconStyleMap;
	}

	@Override
	public byte[] getBackgroundImage() {
		return backgroundImage;
	}

	@Override
	public String getCustomCss() {
		return css;
	}

	public void appendCss(String customCss) {
		if (css == null) {
			css = customCss;
		} else {
			css += "\n" + customCss;
		}
	}

	public void addIconStyle(Class<?> clazz, Object iconStyle) {
		iconStyleMap.put(clazz, iconStyle);
	}

	public void setIconStyleMap(Map<Class<?>, Object> iconStyleMap) {
		this.iconStyleMap = iconStyleMap;
	}

	public void setBackgroundImage(byte[] backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public void setCss(String css) {
		this.css = css;
	}
}
