package org.teamapps.application.api.application;

import org.teamapps.icons.Icon;

public class ApplicationGroup {

	private final Icon icon;
	private final String name;
	private final String titleKey;

	public ApplicationGroup(Icon icon, String name, String titleKey) {
		this.icon = icon;
		this.name = name;
		this.titleKey = titleKey;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	public String getTitleKey() {
		return titleKey;
	}
}
