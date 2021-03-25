/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.api.config;

import org.teamapps.event.Event;

public class ApplicationConfig <CONFIG> {

	private CONFIG config;
	public final Event<CONFIG> onConfigUpdate = new Event<>();


	public ApplicationConfig() {
	}

	public CONFIG getConfig() {
		return config;
	}

	public void setConfig(CONFIG config) {
		this.config = config;
	}

	public void updateConfig(String xml) throws Exception {
		try {
			ApplicationConfigXml<CONFIG> configXml = new ApplicationConfigXml<>();
			CONFIG config = configXml.readConfigFile(xml);
			setConfig(config);
			onConfigUpdate.fire(config);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	public String getConfigXml() {
		if (getConfig() == null) {
			return null;
		}
		ApplicationConfigXml<CONFIG> configXml = new ApplicationConfigXml<>();
		return configXml.getConfigXml(getConfig());
	}
}
