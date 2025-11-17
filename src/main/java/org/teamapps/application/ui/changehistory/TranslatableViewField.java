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
package org.teamapps.application.ui.changehistory;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.ux.component.field.DisplayField;

import java.util.Map;

public class TranslatableViewField extends DisplayField {

	private final ApplicationInstanceData applicationInstanceData;
	private boolean compactView;

	public TranslatableViewField(ApplicationInstanceData applicationInstanceData) {
		super(false, true);
		this.applicationInstanceData = applicationInstanceData;
	}

	public void setValue(TranslatableText value) {
		if (value == null) {
			super.setValue(null);
			return;
		}
		String originalLanguage = value.getOriginalLanguage();
		if (compactView) {
			String msg = originalLanguage + ": " + value.getText();
			super.setValue(msg);
		} else {
			String msg = applicationInstanceData.getLocalized("translatableTextField.originalLanguage") + ": " + TranslatableFieldUtils.getLanguageWithIcon(value.getOriginalLanguage(), true, applicationInstanceData) + "<br>";
			msg += applicationInstanceData.getLocalized("translatableTextField.originalText") + ": " + value.getText() + "<br>";
			msg += applicationInstanceData.getLocalized("translatableTextField.translations") + ":<br>";

			for (Map.Entry<String, String> entry : value.getTranslationMap().entrySet().stream().filter(entry -> !entry.getKey().equals(originalLanguage)).sorted(Map.Entry.comparingByKey()).toList()) {
				msg += TranslatableFieldUtils.getLanguageWithIcon(entry.getKey(), false, applicationInstanceData) + ": " + entry.getValue() + "<br>";
			}
			super.setValue(msg);
		}
	}

	public boolean isCompactView() {
		return compactView;
	}

	public void setCompactView(boolean compactView) {
		this.compactView = compactView;
	}
}
