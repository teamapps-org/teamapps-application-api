package org.teamapps.application.api.application.theme;

import java.util.HashMap;
import java.util.Map;

public class CustomApplicationThemeImpl implements CustomApplicationTheme {

	private Map<Class<?>, Object> iconStyleMap = new HashMap<>();
	private byte[] backgroundImage;
	private String css;

	public CustomApplicationThemeImpl() {
	}

	public CustomApplicationThemeImpl(Map<Class<?>, Object> iconStyleMap, byte[] backgroundImage, String css) {
		this.iconStyleMap = iconStyleMap;
		this.backgroundImage = backgroundImage;
		this.css = css;
	}

	@Override
	public Map<Class<?>, Object> getIconStylesForIconClass() {
		return iconStyleMap;
	}

	@Override
	public byte[] getBackgroundImage() {
		return backgroundImage;
	}

	@Override
	public String getCustomCss() {
		return css;
	}

	public void appendCss(String customCss) {
		if (css == null) {
			css = customCss;
		} else {
			css += "\n" + customCss;
		}
	}

	public void addIconStyle(Class<?> clazz, Object iconStyle) {
		iconStyleMap.put(clazz, iconStyle);
	}

	public void setIconStyleMap(Map<Class<?>, Object> iconStyleMap) {
		this.iconStyleMap = iconStyleMap;
	}

	public void setBackgroundImage(byte[] backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public void setCss(String css) {
		this.css = css;
	}
}
