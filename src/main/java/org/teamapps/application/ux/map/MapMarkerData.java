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
package org.teamapps.application.ux.map;

import org.teamapps.ux.component.map.Location;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.HashMap;
import java.util.Map;

public class MapMarkerData<ENTITY> {

	private final Location location;
	private final Template template;
	private ENTITY entity;
	private Map<String, Object> templateData = new HashMap<>();
	private int offsetX;
	private int offsetY;

	public MapMarkerData(Location location, MapTemplates template) {
		this.location = location;
		this.template = template;
		this.offsetX = template.getOffsetX();
		this.offsetY = template.getOffsetY();
	}

	public MapMarkerData(Location location, Template template) {
		this.location = location;
		this.template = template;
	}

	public MapMarkerData(ENTITY entity, Location location, Template template) {
		this.entity = entity;
		this.location = location;
		this.template = template;
	}

	public void setImage(String image) {
		templateData.put(BaseTemplate.PROPERTY_IMAGE, image);
	}

	public void setCaption(String caption) {
		templateData.put(BaseTemplate.PROPERTY_CAPTION, caption);
	}

	public void setDescription(String description) {
		templateData.put(BaseTemplate.PROPERTY_DESCRIPTION, description);
	}

	public ENTITY getEntity() {
		return entity;
	}

	public void setEntity(ENTITY entity) {
		this.entity = entity;
	}

	public Location getLocation() {
		return location;
	}

	public Template getTemplate() {
		return template;
	}

	public Map<String, Object> getTemplateData() {
		return templateData;
	}

	public void setTemplateData(Map<String, Object> templateData) {
		this.templateData = templateData;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
}
