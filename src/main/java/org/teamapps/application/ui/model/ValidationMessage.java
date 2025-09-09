package org.teamapps.application.ui.model;

import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.Icon;

public class ValidationMessage {

	private final Icon icon;
	private final String message;

	public ValidationMessage(String message) {
		this.icon = ApplicationIcons.ERROR;
		this.message = message;
	}

	public ValidationMessage(Icon icon, String message) {
		this.icon = icon;
		this.message = message;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getMessage() {
		return message;
	}
}
