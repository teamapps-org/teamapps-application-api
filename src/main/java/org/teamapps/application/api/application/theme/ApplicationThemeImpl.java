package org.teamapps.application.api.application.theme;

public class ApplicationThemeImpl implements ApplicationTheme {

	private boolean darkThemePreferred;
	private CustomApplicationTheme darkTheme;
	private CustomApplicationTheme brightTheme;

	public ApplicationThemeImpl() {
	}

	public ApplicationThemeImpl(boolean darkThemePreferred, CustomApplicationTheme darkTheme, CustomApplicationTheme brightTheme) {
		this.darkThemePreferred = darkThemePreferred;
		this.darkTheme = darkTheme;
		this.brightTheme = brightTheme;
	}

	@Override
	public boolean isDarkThemePreferred() {
		return darkThemePreferred;
	}

	@Override
	public CustomApplicationTheme getDarkTheme() {
		return darkTheme;
	}

	@Override
	public CustomApplicationTheme getBrightTheme() {
		return brightTheme;
	}

	public void setDarkThemePreferred(boolean darkThemePreferred) {
		this.darkThemePreferred = darkThemePreferred;
	}

	public void setDarkTheme(CustomApplicationTheme darkTheme) {
		this.darkTheme = darkTheme;
	}

	public void setBrightTheme(CustomApplicationTheme brightTheme) {
		this.brightTheme = brightTheme;
	}
}
