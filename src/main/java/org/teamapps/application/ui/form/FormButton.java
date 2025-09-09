package org.teamapps.application.ui.form;

import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.icons.Icon;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class FormButton<ENTITY> {

	private final Icon icon;
	private final String caption;
	private final String description;
	private BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> allowedFunction;
	private BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> visibilityFunction;
	private BiConsumer<ENTITY, FormEntityState> eventHandler;
	private final Set<FormEntityState> visibleOnStates = new HashSet<>();
	private final List<FormButton<ENTITY>> menuButtons = new ArrayList<>();

	private boolean workspaceButton;
	private String buttonGroupId;

	private boolean visible;

	public FormButton(Icon icon, String caption, String description) {
		this.icon = icon;
		this.caption = caption;
		this.description = description;
	}

	public FormButton(Icon icon, String caption, String description, BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> allowedFunction, BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> visibilityFunction, BiConsumer<ENTITY, FormEntityState> eventHandler, FormEntityState... states) {
		this.icon = icon;
		this.caption = caption;
		this.description = description;
		this.allowedFunction = allowedFunction;
		this.visibilityFunction = visibilityFunction;
		this.eventHandler = eventHandler;
		setVisibilityStates(states);
	}

	public FormButton<ENTITY> setAllowedFunction(BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> allowedFunction) {
		this.allowedFunction = allowedFunction;
		return this;
	}

	public FormButton<ENTITY> setVisibilityFunction(BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> visibilityFunction) {
		this.visibilityFunction = visibilityFunction;
		return this;
	}

	public FormButton<ENTITY> setEventHandler(BiConsumer<ENTITY, FormEntityState> eventHandler) {
		this.eventHandler = eventHandler;
		return this;
	}

	public FormButton<ENTITY> setVisibilityStates(FormEntityState... states) {
		visibleOnStates.clear();
		visibleOnStates.addAll(Arrays.asList(states));
		return this;
	}

	public FormButton<ENTITY> addMenuButton(FormButton<ENTITY> menuButton) {
		menuButtons.add(menuButton);
		return this;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> getAllowedFunction() {
		return allowedFunction;
	}

	public BiFunction<ENTITY, EntityPrivileges<ENTITY>, Boolean> getVisibilityFunction() {
		return visibilityFunction;
	}

	public boolean isAllowed(ENTITY entity, EntityPrivileges<ENTITY> privileges) {
		return allowedFunction.apply(entity, privileges);
	}

	public boolean isVisibilityAllowed(ENTITY entity, EntityPrivileges<ENTITY> privileges) {
		if (visibilityFunction == null) {
			return allowedFunction.apply(entity, privileges);
		}
		return visibilityFunction.apply(entity, privileges);
	}

	public BiConsumer<ENTITY, FormEntityState> getEventHandler() {
		return eventHandler;
	}

	public Set<FormEntityState> getVisibleOnStates() {
		return visibleOnStates;
	}

	public List<FormButton<ENTITY>> getMenuButtons() {
		return menuButtons;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isWorkspaceButton() {
		return workspaceButton;
	}

	public void setWorkspaceButton(boolean workspaceButton) {
		this.workspaceButton = workspaceButton;
	}

	public String getButtonGroupId() {
		return buttonGroupId;
	}

	public void setButtonGroupId(String buttonGroupId) {
		this.buttonGroupId = buttonGroupId;
	}
}
