package org.teamapps.application.api.application.theme;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;

public class ApplicationThemeBuilder {

	public static ApplicationThemeBuilder create() {
		return new ApplicationThemeBuilder();
	}

	private boolean darkThemePreferred;
	private CustomApplicationThemeImpl darkTheme = new CustomApplicationThemeImpl();
	private CustomApplicationThemeImpl brightTheme = new CustomApplicationThemeImpl();

	public ApplicationThemeBuilder darkThemePreferred() {
		darkThemePreferred = true;
		return this;
	}

	public ApplicationThemeBuilder addCss(String css) {
		return addCss(css, true, true);
	}

	public ApplicationThemeBuilder addCss(String css, boolean dark, boolean bright) {
		if (css == null || css.isBlank()) {
			return this;
		}
		if (dark) {
			darkTheme.appendCss(css);
		}
		if (bright) {
			brightTheme.appendCss(css);
		}
		return this;
	}

	public ApplicationThemeBuilder addCssFromResource(String resourceName) {
		return addCssFromResource(resourceName, true, true);
	}

	public ApplicationThemeBuilder addCssFromResource(String resourceName, boolean dark, boolean bright) {
		String css = readStringResource(resourceName);
		return addCss(css, dark, bright);
	}

	public ApplicationThemeBuilder addIconStyle(Class<?> iconClass, Object style) {
		return addIconStyle(iconClass, style, true, true);
	}

	public ApplicationThemeBuilder addIconStyle(Class<?> iconClass, Object style, boolean dark, boolean bright) {
		if (style == null || iconClass == null) {
			return this;
		}
		if (dark) {
			darkTheme.addIconStyle(iconClass, style);
		}
		if (bright) {
			brightTheme.addIconStyle(iconClass, style);
		}
		return this;
	}

	public ApplicationThemeBuilder setBackgroundImage(byte[] bytes) {
		return setBackgroundImage(bytes, true, true);
	}

	public ApplicationThemeBuilder setBackgroundImage(byte[] bytes, boolean dark, boolean bright) {
		if (dark) {
			darkTheme.setBackgroundImage(bytes);
		}
		if (bright) {
			brightTheme.setBackgroundImage(bytes);
		}
		return this;
	}

	public ApplicationThemeBuilder setBackgroundImage(String resourceName) {
		return setBackgroundImage(resourceName, true, true);
	}

	public ApplicationThemeBuilder setBackgroundImage(String resourceName, boolean dark, boolean bright) {
		byte[] bytes = readByteArrayResource(resourceName);
		return setBackgroundImage(bytes, dark, bright);
	}


	public static String readStringResource(String resourceName) {
		try {
			return Resources.toString(Resources.getResource(resourceName), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readByteArrayResource(String resourceName) {
		try {
			return Resources.toByteArray(Resources.getResource(resourceName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationTheme build() {
		return new ApplicationThemeImpl(darkThemePreferred, darkTheme, brightTheme);
	}

}
