package org.teamapps.application.api.application;

import org.teamapps.event.Event;
import org.teamapps.icons.Icon;

public abstract class AbstractApplicationBuilder implements ApplicationBuilder {

	public final Event<Void> onApplicationInstalled = new Event<>();
	public final Event<Void> onApplicationLoaded = new Event<>();
	public final Event<Void> onApplicationUnloaded = new Event<>();
	public final Event<Void> onApplicationUninstalled = new Event<>();

	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;

	public AbstractApplicationBuilder(String name, Icon icon, String titleKey, String descriptionKey) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
	}

	@Override
	public Icon getApplicationIcon() {
		return icon;
	}

	@Override
	public String getApplicationName() {
		return name;
	}

	@Override
	public String getApplicationTitleKey() {
		return titleKey;
	}

	@Override
	public String getApplicationDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public Event<Void> getOnApplicationInstalled() {
		return onApplicationInstalled;
	}

	@Override
	public Event<Void> getOnApplicationLoaded() {
		return onApplicationLoaded;
	}

	@Override
	public Event<Void> getOnApplicationUnloaded() {
		return onApplicationUnloaded;
	}

	@Override
	public Event<Void> getOnApplicationUninstalled() {
		return onApplicationUninstalled;
	}

}
