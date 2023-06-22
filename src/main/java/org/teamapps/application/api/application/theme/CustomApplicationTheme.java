package org.teamapps.application.api.application.theme;

import java.util.Map;

public interface CustomApplicationTheme {

	Map<Class<?>, Object> getIconStylesForIconClass();

	byte[] getBackgroundImage();

	String getCustomCss();

}
