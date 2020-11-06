package org.teamapps.application.api.localization;

public interface ApplicationLocalizationProvider {

	String getLocalized(String key, Object... parameters);
}
