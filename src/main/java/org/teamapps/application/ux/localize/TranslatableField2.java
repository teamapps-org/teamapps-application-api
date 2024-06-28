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
package org.teamapps.application.ux.localize;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.localization.Language;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.form.FormWindow;
import org.teamapps.application.ux.window.DialogeUtils;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.ux.component.absolutelayout.Length;
import org.teamapps.ux.component.field.*;
import org.teamapps.ux.component.field.richtext.RichTextEditor;
import org.teamapps.ux.component.field.richtext.ToolbarVisibilityMode;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.linkbutton.LinkButton;

import java.util.*;
import java.util.function.Function;

public class TranslatableField2 {

	public static final List<String> ORDERED_LANGUAGE_LIST = Arrays.asList("en", "de", "fr", "es", "it", "nl", "pt", "pl", "tr", "bg", "cs", "da", "el", "et", "fi", "hu", "id", "lt", "lv", "no", "ro", "sk", "sl", "sv", "ru", "uk", "ja", "ko", "ar", "zh");
	private final ApplicationInstanceData applicationInstanceData;
	private TranslatableText value;

	private String sourceLanguage;
	private String localizedValue;
	private final boolean showFieldMessage;
	private final TranslatableFieldType fieldType;
	private final Function<TranslatableText, TranslatableText> translationFunction;
	private AbstractField<String> textField;


	public TranslatableField2(TranslatableFieldBuilder builder) {
		this.applicationInstanceData = builder.getApplicationInstanceData();
		this.sourceLanguage = applicationInstanceData.getUser().getLanguage();
		this.showFieldMessage = builder.isShowFieldMessage();
		this.translationFunction = builder.getTranslationFunction();
		this.fieldType = builder.getFieldType();
		int fieldHeight = builder.getFieldHeight();

		switch (fieldType) {
			case TEXT_FIELD -> {
				textField = new TextField();
			}
			case MULTI_LINE_FIELD -> {
				MultiLineTextField multiLineTextField = new MultiLineTextField();
				if (fieldHeight == 0) {
					multiLineTextField.setAdjustHeightToContent(true);
				}
				textField = multiLineTextField;
			}
			case RICH_TEXT_FIELD -> {
				textField = applicationInstanceData.getComponentFactory().createEmbeddedImagesEnabledRichTextEditor("translatable");
			}
		}

		setValue(null);
		if (fieldType != TranslatableFieldType.TEXT_FIELD && fieldHeight > 0) {
			textField.setCssStyle("height", Length.ofPixels(fieldHeight).toCssString());
		}
	}


	public void setValue(TranslatableText value) {
		this.value = value;
		if (value == null) {
			setTextFieldValue(null);
		} else {
			localizedValue = applicationInstanceData.getLocalized(value);
			setTextFieldValue(localizedValue);
		}
		updateFieldMessage();
	}

	public TranslatableText getValue() {
		if (value == null) {
			translatedIfNecessary();
		}
		return value;
	}

	public void translatedIfNecessary() {
		if (translationFunction != null && textField.isValueChangedByClient() && textField.getValue() != null && !textField.getValue().isBlank()) {
			if (value != null && !value.getOriginalLanguage().equals(sourceLanguage)) {
				DialogeUtils.showQuestion(ApplicationIcons.QUESTION,
						"Override existing translations?", //todo
						"You have changed a value that was initially created in a different language. Dou you really want to override all translations?", //todo
						ApplicationIcons.SIGN_WARNING, "Override", //todo
						ApplicationIcons.WINDOW_CLOSE, "Do not override", //todo
						this::performTranslation);
			} else if (value == null) {
				performTranslation();
			}
		}
	}

	private void performTranslation() {
		String text = textField.getValue();
		if (text != null && !text.isBlank()) {
			value = translationFunction.apply(new TranslatableText(text, sourceLanguage));
			textField.setValueChangedByClient(false);
		}
	}

	private void updateFieldMessage() {
		if (!showFieldMessage) {
			return;
		}
		StringBuilder msg;
		if (value == null) {
			msg = new StringBuilder(applicationInstanceData.getLocalized("translatableTextField.emptyField.desc", FlagMap.getTranslatedLanguageWithFlag(sourceLanguage, true, applicationInstanceData)));
		} else {
			String originalLanguage = value.getOriginalLanguage();
			msg = new StringBuilder(applicationInstanceData.getLocalized("translatableTextField.originalLanguage") + ": " + FlagMap.getTranslatedLanguageWithFlag(value.getOriginalLanguage(), true, applicationInstanceData) + "<br>");
			msg.append(applicationInstanceData.getLocalized("translatableTextField.originalText")).append(": ").append(value.getText()).append("<br>");
			msg.append(applicationInstanceData.getLocalized("translatableTextField.translations")).append(":<br>");
			for (Map.Entry<String, String> entry : value.getTranslationMap().entrySet().stream().filter(entry -> !entry.getKey().equals(originalLanguage)).sorted(Map.Entry.comparingByKey()).toList()) {
				msg.append(FlagMap.getTranslatedLanguageWithFlag(entry.getKey(), applicationInstanceData)).append(": ").append(entry.getValue()).append("<br>");
			}
		}
		List<FieldMessage> fieldMessages = Collections.singletonList(new FieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS, FieldMessage.Severity.INFO, msg.toString()));
		textField.setCustomFieldMessages(fieldMessages);
	}

	public AbstractField<String> getTextField() {
		return textField;
	}

	public void setTextFieldValue(String value) {
		textField.setValue(value);
	}

	public boolean isChanged() {
		return !Objects.equals(textField.getValue(), localizedValue);
	}


	public LinkButton createTranslationWindowLinkButton() {
		String caption = applicationInstanceData.getLocalized("translatableTextField.editOrViewTranslations");
		return createTranslationWindowLinkButton(caption);
	}

	public LinkButton createTranslationWindowLinkButton(String caption) {
		LinkButton linkButton = new LinkButton(caption);
		linkButton.onClicked.addListener(this::showTranslationWindow);
		return linkButton;
	}

	public LinkButton createChangeBaseLanguageLinkButton() {
		String caption = applicationInstanceData.getLocalized("translatableTextField.changeLanguage") + " (" + FlagMap.getTranslatedLanguageWithFlag(sourceLanguage, applicationInstanceData) + ")";
		return createChangeBaseLanguageLinkButton(caption);
	}

	public LinkButton createChangeBaseLanguageLinkButton(String caption) {
		LinkButton linkButton = new LinkButton(caption);
		linkButton.onClicked.addListener(this::showChangeBaseLanguageWindow);
		return linkButton;
	}

	public void addToForm(String caption, boolean showChangeBaseLanguageLink, boolean showOpenTranslationWindowLink, ResponsiveFormLayout formLayout) {
		formLayout.addLabelAndField(null, caption, textField);
		if (showChangeBaseLanguageLink) {
			formLayout.addLabelAndComponent(null, null, createChangeBaseLanguageLinkButton());
		}
		if (showOpenTranslationWindowLink) {
			formLayout.addLabelAndComponent(null, null, createTranslationWindowLinkButton());
		}
	}


	public void showChangeBaseLanguageWindow() {

	}

	public void showTranslationWindow() {
		FormWindow formWindow = new FormWindow(ApplicationIcons.EARTH, applicationInstanceData.getLocalized(Dictionary.TRANSLATION), true, applicationInstanceData);
		formWindow.addSection().setCollapsible(false).setDrawHeaderLine(false);
		Map<String, AbstractField<String>> fieldMap = new LinkedHashMap<>();

		TranslatableText translatableText = value != null ? value : new TranslatableText(textField.getValue(), sourceLanguage);
		translatableText.setTranslation(textField.getValue(), sourceLanguage);

		if (value != null) {
			createField(value.getOriginalLanguage(), fieldMap, translatableText, formWindow);
		} else {
			createField(sourceLanguage, fieldMap, translatableText, formWindow);
		}
		for (String rankedLanguage : applicationInstanceData.getUser().getRankedLanguages()) {
			createField(rankedLanguage, fieldMap, translatableText, formWindow);
		}
		for (String language : ORDERED_LANGUAGE_LIST) {
			createField(language, fieldMap, translatableText, formWindow);
		}


		formWindow.addOkButton().onClick.addListener(() -> {
			if (fieldMap.get(sourceLanguage) != null && fieldMap.get(sourceLanguage).getValue() != null) {
				fieldMap.entrySet().stream()
						.filter(e -> e.getValue().getValue() != null && !e.getValue().getValue().isBlank())
						.forEach(e -> translatableText.setTranslation(e.getValue().getValue(), e.getKey()));
				setValue(translatableText);
			}
			formWindow.close();
		});

		if (translationFunction != null && translatableText.getText() != null && !translatableText.getText().isBlank()) {
			formWindow.addButton(ApplicationIcons.GEARWHEEL, applicationInstanceData.getLocalized("apps.translate")).onClick.addListener(() -> {
				TranslatableText inputText = new TranslatableText(translatableText.getText(), translatableText.getOriginalLanguage());
				fieldMap.entrySet().stream()
						.filter(e -> e.getValue().getValue() != null && !e.getValue().getValue().isBlank())
						.forEach(entry -> inputText.setTranslation(entry.getValue().getValue(), entry.getKey()));
				TranslatableText text = translationFunction.apply(inputText);
				text.getTranslationMap().entrySet().stream().forEach(entry -> {
					AbstractField<String> field = fieldMap.get(entry.getKey());
					field.setValue(entry.getValue());
				});
			});
		}


		formWindow.addButton(ApplicationIcons.DELETE, applicationInstanceData.getLocalized(Dictionary.REMOVE)).onClick.addListener(() -> {
			fieldMap.values().forEach(f -> f.setValue(null));
		});

		formWindow.addCancelButton();
		formWindow.show();
	}

	private void createField(String languageIso, Map<String, AbstractField<String>> fieldMap, TranslatableText translatableText, FormWindow formWindow) {
		if (!fieldMap.containsKey(languageIso)) {
			AbstractField<String> field = null;
			switch (fieldType) {
				case TEXT_FIELD -> {
					field = new TextField();
				}
				case MULTI_LINE_FIELD -> {
					MultiLineTextField multiLineTextField = new MultiLineTextField();
					multiLineTextField.setAdjustHeightToContent(true);
					field = multiLineTextField;
				}
				case RICH_TEXT_FIELD -> {
					RichTextEditor richTextEditor = new RichTextEditor();
					richTextEditor.setToolbarVisibilityMode(ToolbarVisibilityMode.VISIBLE_IF_FOCUSED);
					field = richTextEditor;
				}
			}
			Language language = Language.getLanguageByIsoCode(languageIso);
			Icon icon = language != null ? language.getIcon() : null;
			String name = language != null ? language.getLanguageLocalized(applicationInstanceData) : languageIso;
			formWindow.addField(icon, name, field);
			fieldMap.put(languageIso, field);
			String value = translatableText.getTranslation(languageIso);
			if (value != null) {
				field.setValue(value);
			}
		}
	}


}
