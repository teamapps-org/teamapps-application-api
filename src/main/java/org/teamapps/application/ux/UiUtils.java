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
package org.teamapps.application.ux;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.window.BaseDialogue;
import org.teamapps.common.format.Color;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.field.DisplayField;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.field.combobox.TagBoxWrappingMode;
import org.teamapps.ux.component.field.combobox.TagComboBox;
import org.teamapps.ux.component.format.FontStyle;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.gridtemplate.BadgeElement;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UiUtils {

	public static void showNotification(Icon icon, String title) {
		SessionContext.current().showNotification(icon, title);
	}

	public static void showSaveNotification(boolean success, ApplicationInstanceData applicationInstanceData) {
		SessionContext.current().showNotification(
				success ? ApplicationIcons.OK : ApplicationIcons.ERROR,
				success ? applicationInstanceData.getLocalized(Dictionary.RECORD_SUCCESSFULLY_SAVED) : applicationInstanceData.getLocalized(Dictionary.ERROR_WHEN_SAVING)
		);
	}

	public static void showDeleteQuestion(Runnable onConfirmation, ApplicationInstanceData applicationInstanceData) {
		showQuestion(ApplicationIcons.DELETE, applicationInstanceData.getLocalized(Dictionary.DELETE), applicationInstanceData.getLocalized(Dictionary.SENTENCE_DO_YOU_REALLY_WANT_TO_DELETE_THE_RE__), onConfirmation, applicationInstanceData);
	}

	public static void showDeleteQuestion(String question, Runnable onConfirmation, ApplicationInstanceData applicationInstanceData) {
		showQuestion(ApplicationIcons.DELETE, applicationInstanceData.getLocalized(Dictionary.DELETE), question, onConfirmation, applicationInstanceData);
	}

	public static void showQuestion(Icon icon, String title, String text, Runnable onConfirmation, ApplicationInstanceData applicationInstanceData) {
		BaseDialogue.showOkCancel(icon, title, text, applicationInstanceData).addListener(result -> {
			if (result) {
				onConfirmation.run();
			}
		});
	}

	public static TemplateField<String> createBadgeField(Color textColor, Color backgroundColor) {
		GridTemplate tpl = new GridTemplate()
				.addColumn(new GridColumn(SizingPolicy.AUTO))
				.addColumn(new GridColumn(SizingPolicy.FRACTION))
				.addRow(SizingPolicy.AUTO)
				.setPadding(Spacing.px(2, 5))
				.addElement(new BadgeElement(BaseTemplate.PROPERTY_BADGE, 0, 0)
						.setFontStyle(new FontStyle(0.9f, textColor))
						.setBackgroundColor(backgroundColor)
				);
		TemplateField<String> templateField = new TemplateField<>(tpl);
		templateField.setPropertyProvider((s, propertyNames) -> {
			Map<String, Object> map = PropertyData.createEmpty();
			map.put(BaseTemplate.PROPERTY_BADGE, s);
			return map;
		});
		return templateField;
	}

	public static <TYPE> TemplateField<TYPE> createTemplateField(Template template, PropertyProvider<TYPE> propertyProvider) {
		TemplateField<TYPE> templateField = new TemplateField<>(template);
		templateField.setPropertyProvider(propertyProvider);
		return templateField;
	}

	public static TemplateField<Boolean> createBooleanTemplateField(Icon trueIcon, String trueTitle, Icon falseIcon, String falseTitle) {
		TemplateField<Boolean> templateField = new TemplateField<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		templateField.setPropertyExtractor((value, propertyName) -> switch (propertyName) {
			case BaseTemplate.PROPERTY_ICON -> value ? trueIcon : falseIcon;
			case BaseTemplate.PROPERTY_CAPTION -> value ? trueTitle : falseTitle;
			default -> null;
		});
		return templateField;
	}

	public static TemplateField<String> createIconFixedIconTemplateField(Icon icon) {
		TemplateField<String> templateField = new TemplateField<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		templateField.setPropertyExtractor((value, propertyName) -> switch (propertyName) {
			case BaseTemplate.PROPERTY_ICON -> icon;
			case BaseTemplate.PROPERTY_CAPTION -> value;
			default -> null;
		});
		return templateField;
	}

	public static TemplateField<String> createSingleValueTemplateField(Icon icon, String title) {
		TemplateField<String> templateField = new TemplateField<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		templateField.setPropertyExtractor((value, propertyName) -> switch (propertyName) {
			case BaseTemplate.PROPERTY_ICON -> icon;
			case BaseTemplate.PROPERTY_CAPTION -> title;
			default -> null;
		});
		templateField.setValue(title);
		return templateField;
	}

	public static TextField createSingleValueTextField(String value) {
		TextField textField = new TextField();
		textField.setValue(value);
		textField.setEditingMode(FieldEditingMode.READONLY);
		return textField;
	}

	public static DisplayField createSingleValueDisplayField(String html) {
		DisplayField displayField = new DisplayField(false, true);
		displayField.setValue(html);
		return displayField;
	}

	public static TagComboBox<String> createSingleValueTagComboBox(Icon icon, List<String> values) {
		TagComboBox<String> tagComboBox = new TagComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		tagComboBox.setValue(values);
		tagComboBox.setPropertyExtractor((s, propertyName) -> switch (propertyName) {
			case BaseTemplate.PROPERTY_ICON -> icon;
			case BaseTemplate.PROPERTY_CAPTION -> s;
			default -> null;
		});
		tagComboBox.setEditingMode(FieldEditingMode.READONLY);
		return tagComboBox;
	}

	public static <TYPE> TagComboBox<TYPE> createTagComboBox(Template template, PropertyProvider<TYPE> propertyProvider) {
		TagComboBox<TYPE> tagComboBox = new TagComboBox<>(template);
		tagComboBox.setWrappingMode(TagBoxWrappingMode.SINGLE_TAG_PER_LINE);
		tagComboBox.setPropertyProvider(propertyProvider);
		return tagComboBox;
	}

	public static <RECORD> Function<RECORD, String> createRecordQueryStringFunction(PropertyProvider<RECORD> propertyProvider, String... properties) {
		Set<String> keys = properties != null && properties.length > 0 ? new HashSet<>(Arrays.asList(properties)) : new HashSet<>(Arrays.asList(BaseTemplate.PROPERTY_CAPTION));
		return record -> {
			Map<String, Object> values = propertyProvider.getValues(record, keys);
			return values.entrySet().stream()
					.filter(entry -> keys.contains(entry.getKey()))
					.map(Map.Entry::getValue)
					.filter(Objects::nonNull)
					.map(v -> (String) v)
					.collect(Collectors.joining(", ")).toLowerCase();
		};
	}

	public static <RECORD> Function<RECORD, String> createRecordToStringFunction(PropertyProvider<RECORD> propertyProvider) {
		return createRecordToStringFunction(propertyProvider, BaseTemplate.PROPERTY_CAPTION);
	}

	public static <RECORD> Function<RECORD, String> createRecordToStringFunction(PropertyProvider<RECORD> propertyProvider, String property) {
		return record -> {
			Object value = propertyProvider.getValues(record, Collections.singleton(property)).get(property);
			return value != null ? (String) value : "";
		};
	}

	public static <RECORD> BiFunction<RECORD, String, Boolean> createRecordFilterFunction(PropertyProvider<RECORD> propertyProvider, String... properties) {
		Function<RECORD, String> queryStringFunction = createRecordQueryStringFunction(propertyProvider, properties);
		return (record, s) -> s == null || s.isBlank() || queryStringFunction.apply(record).contains(s.toLowerCase());
	}

	public static <RECORD> PropertyProvider<RECORD> createPropertyProvider(Function<RECORD, Icon> icon, Function<RECORD, String> image, Function<RECORD, String> caption, Function<RECORD, String> description) {
		return (record, collection) -> {
			Map<String, Object> map = new HashMap<>();
			if (record == null) {
				return map;
			}
			map.put(BaseTemplate.PROPERTY_ICON, icon != null ? icon.apply(record) : null);
			map.put(BaseTemplate.PROPERTY_IMAGE, image != null ? image.apply(record) : null);
			map.put(BaseTemplate.PROPERTY_CAPTION, caption != null ? caption.apply(record) : null);
			map.put(BaseTemplate.PROPERTY_DESCRIPTION, description != null ? description.apply(record) : null);
			return map;
		};
	}

}
