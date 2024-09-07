/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
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
package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.tools.RecordModelBuilder;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.absolutelayout.Length;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

public class FormPanel {

	private final ApplicationInstanceData applicationInstanceData;
	private Panel panel;
	private Toolbar toolbar;
	private ToolbarButtonGroup currentButtonGroup;
	private ToolbarButton addButton;
	private ToolbarButton editButton;
	private ToolbarButton deleteButton;
	private int topSpace = 90;
	private int maxHeight = 300;

	public FormPanel(ApplicationInstanceData applicationInstanceData) {
		this(applicationInstanceData, null);
	}

	public FormPanel(ApplicationInstanceData applicationInstanceData, Component content) {
		this.applicationInstanceData = applicationInstanceData;
		panel = new Panel();
		toolbar = new Toolbar();
		panel.setHideTitleBar(true);
		panel.setToolbar(toolbar);
		setHeight(75);
		currentButtonGroup = toolbar.addButtonGroup(new ToolbarButtonGroup());
		setContent(content);
	}

	public void setPanelTitleAndIcon(Icon icon, String title) {
		panel.setHideTitleBar(false);
		panel.setIcon(icon);
		panel.setTitle(title);
	}

	public void setContent(Component content) {
		panel.setContent(content);
	}

	public void setHeight(int height) {
		panel.setCssStyle("height", Length.ofPixels(height).toCssString());
	}

	public void addToFormAsSection(ResponsiveFormLayout formLayout, Icon icon, String caption) {
		panel.setIcon(icon);
		panel.setTitle(caption);
		panel.setHideTitleBar(false);
		formLayout.addSection().setCollapsible(false).setDrawHeaderLine(false).setHideWhenNoVisibleFields(true);
		formLayout.addLabelAndComponent(panel);
	}

	public <RECORD> void setTable(Table<RECORD> table, RecordModelBuilder<RECORD> recordModelBuilder, Icon panelIcon, String panelTitle, boolean autoHeight, boolean autoEditButtonVisibility, boolean addAllEditButtons) {
		panel.setHideTitleBar(false);
		//topSpace += 10;
		panel.setIcon(panelIcon);
		recordModelBuilder.attachSearchField(panel);
		recordModelBuilder.attachViewCountHandler(panel, () -> panelTitle);
		setTable(table, autoHeight, autoEditButtonVisibility, addAllEditButtons);
	}

	public void setTable(Table<?> table, boolean autoHeight, boolean autoEditButtonVisibility, boolean addAllEditButtons) {
		setContent(table);
		if (autoHeight) {
			setAutoHeight(table);
		}
		if (autoEditButtonVisibility) {
			setAutoDisplayEditButtons(table);
		}
		if (addAllEditButtons) {
			addCreateButton();
			addEditButton();
			addDeleteButton();
		}
	}

	public void setAutoHeight(Table<?> table) {
		table.getCount().onChanged().addListener(count -> {
			setHeight(Math.min(maxHeight, topSpace + ((table.getRowHeight() + 2) * count)));
		});
	}

	public void setAutoDisplayEditButtons(Table<?> table) {
		table.onSingleRowSelected.addListener(record -> {
			if (editButton != null) {
				editButton.setVisible(true);
			}
			if (deleteButton != null) {
				deleteButton.setVisible(true);
			}
		});
		table.getModel().onAllDataChanged().addListener(() -> {
			if (editButton != null) {
				editButton.setVisible(false);
			}
			if (deleteButton != null) {
				deleteButton.setVisible(false);
			}
		});
		table.getCount().onChanged().addListener(count -> {
			if (count == 0) {
				if (editButton != null) {
					editButton.setVisible(false);
				}
				if (deleteButton != null) {
					deleteButton.setVisible(false);
				}
			}
		});
	}

	public ToolbarButtonGroup addButtonGroup() {
		currentButtonGroup = new ToolbarButtonGroup();
		toolbar.addButtonGroup(currentButtonGroup);
		return currentButtonGroup;
	}

	public ToolbarButton addButton(Icon icon, String title) {
		ToolbarButton button = ToolbarButton.createTiny(icon, title);
		currentButtonGroup.addButton(button);
		return button;
	}

	public ToolbarButton addCreateButton() {
		return addCreateButton(getLocalized(Dictionary.ADD));
	}

	public ToolbarButton addCreateButton(String title) {
		addButton = ToolbarButton.createTiny(ApplicationIcons.ADD, title);
		currentButtonGroup.addButton(addButton);
		return addButton;
	}

	public ToolbarButton addEditButton() {
		return addEditButton(getLocalized(Dictionary.EDIT));
	}

	public ToolbarButton addEditButton(String title) {
		editButton = ToolbarButton.createTiny(ApplicationIcons.EDIT, title);
		editButton.setVisible(false);
		currentButtonGroup.addButton(editButton);
		return editButton;
	}

	public ToolbarButton addDeleteButton() {
		return addDeleteButton(getLocalized(Dictionary.DELETE));
	}

	public ToolbarButton addDeleteButton(String title) {
		deleteButton = ToolbarButton.createTiny(ApplicationIcons.ERROR, title);
		deleteButton.setVisible(false);
		currentButtonGroup.addButton(deleteButton);
		return deleteButton;
	}

	private String getLocalized(String key) {
		return applicationInstanceData.getLocalized(key);
	}

	public Panel getPanel() {
		return panel;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public ToolbarButtonGroup getCurrentButtonGroup() {
		return currentButtonGroup;
	}

	public ToolbarButton getAddButton() {
		return addButton;
	}

	public ToolbarButton getEditButton() {
		return editButton;
	}

	public ToolbarButton getDeleteButton() {
		return deleteButton;
	}

	public void setTopSpace(int topSpace) {
		this.topSpace = topSpace;
	}

	public void setMaxHeight(int maxSpace) {
		this.maxHeight = maxSpace;
	}
}
