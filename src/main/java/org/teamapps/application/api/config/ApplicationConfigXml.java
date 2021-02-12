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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApplicationConfigXml<CONFIG> {

	public CONFIG readConfigFile(String xml) {
		XStream xstream = createXStream();
		return (CONFIG) xstream.fromXML(xml);
	}

	public CONFIG readConfigFile(File xml) {
		XStream xstream = createXStream();
		return (CONFIG) xstream.fromXML(xml);
	}

	public String getConfigXml(CONFIG config) {
		XStream xStream = createXStream();

		return xStream.toXML(config);
	}

	public void writeConfig(CONFIG config, File file) throws IOException {
		String xml = getConfigXml(config);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		bos.write(xml.getBytes(StandardCharsets.UTF_8));
		bos.close();
	}

	private static XStream createXStream() {
		XStream xstream = new XStream(new DomDriver()) {
			@Override
			protected void setupConverters() {
			}
		};
		xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()), XStream.PRIORITY_VERY_LOW);
		xstream.registerConverter(new BooleanConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new IntConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new StringConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new CollectionConverter(xstream.getMapper()), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new FileConverter());
		xstream.addPermission(NullPermission.NULL);
		xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream.ignoreUnknownElements();
		return xstream;
	}
}
