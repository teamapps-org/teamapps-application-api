package org.teamapps.application.api.application.theme;

import java.util.Map;

public interface ApplicationTheme {

	boolean isDarkThemePreferred();

	CustomApplicationTheme getDarkTheme();

	CustomApplicationTheme getBrightTheme();


}
