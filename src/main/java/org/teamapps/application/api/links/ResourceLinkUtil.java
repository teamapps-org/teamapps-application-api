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
package org.teamapps.application.api.links;

import org.teamapps.universaldb.index.file.FileValue;
import org.teamapps.universaldb.index.file.value.MimeType;
import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.InputStreamResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.session.SessionContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.function.Supplier;

public class ResourceLinkUtil {

	public static String createFileResourceLink(FileValue value) {
	return createFileResourceLink(value, null);
	}

	public static String createFileResourceLink(FileValue value, String overrideFileName) {
		if (value == null || value.getSize() == 0) {
			return null;
		} else {
			Supplier<InputStream> inputStreamSupplier = () -> {
				try (InputStream stream = value.getInputStream()) {
					return stream;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
			String name = overrideFileName != null ? overrideFileName + "." + value.getFileExtension() : value.getFileName();
			InputStreamResource inputStreamResource = null;
			MimeType mimeTypeData = value.getMimeTypeData();
			if (mimeTypeData != null) {
				String mimeType = mimeTypeData.getMimeType();
				inputStreamResource = new InputStreamResource(inputStreamSupplier, value.getSize(), name) {
					@Override
					public String getMimeType() {
						return mimeType;
					}

					public Date getLastModified() {
						return new Date();
					}
				};
			} else {
				inputStreamResource = new InputStreamResource(inputStreamSupplier, value.getSize(), name);
			}
			return SessionContext.current().createResourceLink(inputStreamResource, value.getHash());
		}
	}

	public static String createFileResourceLink(File file) {
		return createFileResourceLink(file, file.getName());
	}

	public static String createFileResourceLink(File file, String name) {
		if (file == null || file.length() == 0) {
			return null;
		} else {
			return SessionContext.current().createResourceLink(new FileResource(file,name));
		}
	}
}
