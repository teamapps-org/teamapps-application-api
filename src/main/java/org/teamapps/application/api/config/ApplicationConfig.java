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
package org.teamapps.application.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.teamapps.event.Event;

public class ApplicationConfig<CONFIG> {

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

	public void updateConfig(CONFIG config) {
		this.config = config;
		onConfigUpdate.fire(config);
	}

	public void updateConfig(String xml, ClassLoader classLoader) throws Exception {
		try {
			XmlMapper xmlMapper = createXmlMapper(classLoader);
			Class<?> configClass = config.getClass();
			CONFIG config = (CONFIG)  xmlMapper.readValue(xml, configClass);
			setConfig(config);
			onConfigUpdate.fire(config);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	public String getConfigXml(ClassLoader classLoader) {
		if (getConfig() == null) {
			return null;
		}
		try {
			XmlMapper xmlMapper = createXmlMapper(classLoader);
			return xmlMapper.writeValueAsString(getConfig());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private XmlMapper createXmlMapper(ClassLoader classLoader) {
		XmlMapper xmlMapper = new XmlMapper();
		TypeFactory typeFactory = TypeFactory.defaultInstance().withClassLoader(classLoader);
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		xmlMapper.setTypeFactory(typeFactory);
		return xmlMapper;
	}

}
