package org.teamapps.application.ux;

import org.teamapps.icons.Icon;
import org.teamapps.ux.component.template.BaseTemplate;

import java.util.HashMap;
import java.util.Map;

public class PropertyData {

	public static Map<String, Object> createEmpty() {
		return new HashMap<>();
	}

	public static Map<String, Object> create(Icon icon, String title) {
		return create(title, null, icon, null);
	}

	public static Map<String, Object> create(Icon icon, String title, String description) {
		return create(title, description, icon, null);
	}

	public static Map<String, Object> create(String title, String description, Icon icon, String image) {
		Map<String, Object> map = new HashMap<>();
		if (icon != null) {
			map.put(BaseTemplate.PROPERTY_ICON, icon);
		}
		if (image != null) {
			map.put(BaseTemplate.PROPERTY_IMAGE, image);
		}
		if (title != null) {
			map.put(BaseTemplate.PROPERTY_CAPTION, title);
		}
		if (description != null) {
			map.put(BaseTemplate.PROPERTY_DESCRIPTION, description);
		}
		return map;
	}
}
