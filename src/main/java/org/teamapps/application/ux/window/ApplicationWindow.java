/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.ux.window;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.ApplicationInstanceDataMethods;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.session.SessionContext;

public class ApplicationWindow implements ApplicationInstanceDataMethods {

	private final Window window;
	private final boolean smallToolbarButtons;
	private final ApplicationInstanceData applicationInstanceData;
	private Toolbar toolbar;
	private ToolbarButtonGroup currentButtonGroup;
	private ToolbarButton saveButton;
	private ToolbarButton okButton;
	private ToolbarButton cancelButton;

	public ApplicationWindow(Icon icon, String title, ApplicationInstanceData applicationInstanceData) {
		this(icon, title, false, applicationInstanceData);
	}

	public ApplicationWindow(Icon icon, String title, boolean smallToolbarButtons, ApplicationInstanceData applicationInstanceData) {
		this.smallToolbarButtons = smallToolbarButtons;
		this.applicationInstanceData = applicationInstanceData;
		window = new Window(icon, title, 800, 600, null);
		window.setResizable(true);
		toolbar = new Toolbar();
		window.setToolbar(toolbar);
		window.setMaximizable(true);
		window.setCloseable(true);
		window.setCloseOnEscape(true);
		window.enableAutoHeight();
		currentButtonGroup = toolbar.addButtonGroup(new ToolbarButtonGroup());
	}

	public ToolbarButtonGroup addButtonGroup() {
		currentButtonGroup = new ToolbarButtonGroup();
		toolbar.addButtonGroup(currentButtonGroup);
		return currentButtonGroup;
	}

	public ToolbarButton addButton(Icon icon, String title) {
		return addButton(icon, title, title);
	}

	public ToolbarButton addButton(Icon icon, String title, String description) {
		ToolbarButton button = smallToolbarButtons ? ToolbarButton.createSmall(icon, title) : ToolbarButton.create(icon, title, description);
		currentButtonGroup.addButton(button);
		return button;
	}

	public ToolbarButton addSaveButton() {
		return addSaveButton(getLocalized(Dictionary.SAVE), getLocalized(Dictionary.SAVE_AND_CLOSE_WINDOW));
	}

	public ToolbarButton addSaveButton(String title, String description) {
		saveButton = smallToolbarButtons ? ToolbarButton.createSmall(ApplicationIcons.FLOPPY_DISK, title) : ToolbarButton.create(ApplicationIcons.FLOPPY_DISK, title, description);
		currentButtonGroup.addButton(saveButton);
		return saveButton;
	}

	public ToolbarButton addOkButton() {
		return addOkButton(getLocalized(Dictionary.O_K), getLocalized(Dictionary.O_K));
	}

	public ToolbarButton addOkButton(String title, String description) {
		okButton = smallToolbarButtons ? ToolbarButton.createSmall(ApplicationIcons.OK, title) : ToolbarButton.create(ApplicationIcons.OK, title, description);
		currentButtonGroup.addButton(okButton);
		return okButton;
	}

	public ToolbarButton addCancelButton() {
		return addCancelButton(getLocalized(Dictionary.CANCEL), getLocalized(Dictionary.CANCEL_AND_CLOSE_WINDOW));
	}

	public ToolbarButton addCancelButton(String title, String description) {
		cancelButton = smallToolbarButtons ? ToolbarButton.createSmall(ApplicationIcons.WINDOW_CLOSE, title) : ToolbarButton.create(ApplicationIcons.WINDOW_CLOSE, title, description);
		currentButtonGroup.addButton(cancelButton);
		cancelButton.onClick.addListener(() -> window.close());
		return cancelButton;
	}

	public void setContent(Component content) {
		window.setContent(content);
	}

	public void setWindowRelativeSize(float relativeWidth, float relativeHeight) {
		int windowWidth = (int) Math.max(800, Math.min(1600, SessionContext.current().getClientInfo().getViewPortWidth() * relativeWidth));
		int windowHeight = (int) Math.max(600, Math.min(1200, SessionContext.current().getClientInfo().getScreenHeight() * relativeHeight));
		window.setSize(windowWidth, windowHeight);

	}

	public void setWindowPreferredSize(int width, int height, float minRelativeMargin) {
		int windowWidth = (int) Math.min(width, SessionContext.current().getClientInfo().getViewPortWidth() * (1 - minRelativeMargin));
		int windowHeight = (int) Math.min(height, SessionContext.current().getClientInfo().getScreenHeight() * (1 - minRelativeMargin));
		window.setSize(windowWidth, windowHeight);
	}

	public void setWindowSize(int width, int height) {
		window.setSize(width, height);
	}

	public void show() {
		window.show(300);
	}

	public void close() {
		window.close();
	}

	public ToolbarButtonGroup getCurrentButtonGroup() {
		return currentButtonGroup;
	}

	public ToolbarButton getSaveButton() {
		return saveButton;
	}

	public ToolbarButton getOkButton() {
		return okButton;
	}

	public ToolbarButton getCancelButton() {
		return cancelButton;
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	public Window getWindow() {
		return window;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}
}
