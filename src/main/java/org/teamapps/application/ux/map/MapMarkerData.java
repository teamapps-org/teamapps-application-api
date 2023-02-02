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
