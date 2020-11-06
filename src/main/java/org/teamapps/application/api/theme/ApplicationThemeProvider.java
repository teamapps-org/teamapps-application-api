package org.teamapps.application.api.theme;

import org.teamapps.icons.api.Icon;

public interface ApplicationThemeProvider {

	Icon getStyledIcon(Icon icon);

	Icon getStyledIcon(Icon icon, Icon subIcon);
}
