package org.teamapps.application.api.application.perspective;

import org.teamapps.icons.Icon;

public class PerspectiveDataImpl implements PerspectiveData {

	private String name;
	private Icon icon;
	private String titleKey;
	private String descriptionKey;

	public PerspectiveDataImpl(String name, Icon icon, String titleKey, String descriptionKey) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getTitleKey() {
		return titleKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}
}
