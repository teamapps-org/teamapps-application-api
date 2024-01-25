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
import org.teamapps.application.ux.window.ApplicationWindow;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;

import java.util.ArrayList;
import java.util.List;

public class FormWindow extends ApplicationWindow {

	private ResponsiveForm responsiveForm;
	private ResponsiveFormLayout formLayout;
	private List<AbstractField> fields = new ArrayList<>();

	public FormWindow(Icon icon, String title, ApplicationInstanceData applicationInstanceData) {
		super(icon, title, applicationInstanceData);
		init();
	}

	private void init() {
		responsiveForm = new ResponsiveForm<>(100, 150, 0);
		formLayout = responsiveForm.addResponsiveFormLayout(500);
		setContent(responsiveForm);
	}

	public ResponsiveFormSection addSection() {
		return formLayout.addSection().setDrawHeaderLine(false).setCollapsible(false);
	}

	public ResponsiveFormSection addSection(Icon icon, String title) {
		return formLayout.addSection(icon, title);
	}

	public void addField(String label, Component field) {
		addField(null, label, field);
	}

	public ResponsiveFormLayout.LabelAndField addField(Icon icon, String label, Component field) {
		ResponsiveFormLayout.LabelAndField labelAndField = formLayout.addLabelAndComponent(icon, label, field);
		if (field instanceof AbstractField) {
			fields.add((AbstractField) field);
		}
		return labelAndField;
	}

	public ResponsiveForm getResponsiveForm() {
		return responsiveForm;
	}

	public ResponsiveFormLayout getFormLayout() {
		return formLayout;
	}

	public List<AbstractField> getFields() {
		return fields;
	}
}
