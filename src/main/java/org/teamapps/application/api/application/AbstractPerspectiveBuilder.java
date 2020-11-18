package org.teamapps.application.api.application;

import org.teamapps.icons.Icon;

public abstract class AbstractPerspectiveBuilder implements PerspectiveBuilder {
	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;

	public AbstractPerspectiveBuilder(String name, Icon icon, String titleKey, String descriptionKey) {
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
