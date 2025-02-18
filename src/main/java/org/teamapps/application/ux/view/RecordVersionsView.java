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
package org.teamapps.application.ux.view;

import org.apache.commons.io.FileUtils;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.window.ApplicationWindow;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.data.extract.ValueExtractor;
import org.teamapps.icons.Icon;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.universaldb.index.FieldIndex;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.file.FileValue;
import org.teamapps.universaldb.index.reference.multi.MultiReferenceIndex;
import org.teamapps.universaldb.index.reference.single.SingleReferenceIndex;
import org.teamapps.universaldb.index.reference.value.ResolvedMultiReferenceUpdate;
import org.teamapps.universaldb.index.transaction.resolved.ResolvedTransactionRecordType;
import org.teamapps.universaldb.index.transaction.resolved.ResolvedTransactionRecordValue;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.universaldb.index.versioning.RecordUpdate;
import org.teamapps.universaldb.model.EnumFieldModel;
import org.teamapps.universaldb.model.FieldType;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.StandardLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.field.*;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.field.combobox.TagBoxWrappingMode;
import org.teamapps.ux.component.field.combobox.TagComboBox;
import org.teamapps.ux.component.field.datetime.InstantDateTimeField;
import org.teamapps.ux.component.field.datetime.LocalDateField;
import org.teamapps.ux.component.field.datetime.LocalTimeField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.format.*;
import org.teamapps.ux.component.table.ListTable;
import org.teamapps.ux.component.table.ListTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.table.TableColumn;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.component.template.gridtemplate.IconElement;
import org.teamapps.ux.component.template.gridtemplate.ImageElement;
import org.teamapps.ux.component.template.gridtemplate.TextElement;
import org.teamapps.ux.component.toolbar.ToolbarButton;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordVersionsView<ENTITY extends Entity<?>> {

	private final ENTITY entity;
	private final ApplicationInstanceData applicationInstanceData;
	private final AbstractUdbEntity<ENTITY> record;
	private final TableIndex tableIndex;
	private List<RecordUpdate> recordUpdates;
	private ResponsiveApplication responsiveApplication;
	private View leftView;
	private View centerView;
	private View rightView;
	private List<RecordVersionViewFieldData> viewFields = new ArrayList<>();


	public RecordVersionsView(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		this.entity = entity;
		this.applicationInstanceData = applicationInstanceData;
		this.record = (AbstractUdbEntity<ENTITY>) entity;
		this.tableIndex = record.getTableIndex();
		this.recordUpdates = record.getRecordUpdates();

	}

	public static String getFirstUpper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String createTitleFromCamelCase(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i < 3) {
				sb.append(c);
			} else {
				if (Character.isUpperCase(c)) {
					sb.append(" ");
				}
				sb.append(c);
			}
		}
		return getFirstUpper(sb.toString());
	}

	public RecordVersionsView addField(String fieldName, String fieldTitle) {
		viewFields.add(new RecordVersionViewFieldData(getField(fieldName), fieldName, fieldTitle));
		return this;
	}

	public RecordVersionsView addReferenceField(String fieldName, String fieldTitle, Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord) {

		viewFields.add(new RecordVersionViewFieldData(getField(fieldName), fieldName, fieldTitle, referencedRecordIdToTemplateRecord));
		return this;
	}

	public RecordVersionsView addReferenceField(String fieldName, String fieldTitle, Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord, Template template) {
		viewFields.add(new RecordVersionViewFieldData(getField(fieldName), fieldName, fieldTitle, referencedRecordIdToTemplateRecord, template));
		return this;
	}

	public RecordVersionsView addCustomField(String fieldName, String fieldTitle, AbstractField<?> formField, Function<Object, Object> formFieldDataProvider, AbstractField<?> tableField, Function<Object, Object> tableFieldDataProvider) {
		viewFields.add(new RecordVersionViewFieldData(getField(fieldName), fieldName, fieldTitle, formField, formFieldDataProvider, tableField, tableFieldDataProvider));
		return this;
	}

	public RecordVersionsView addCustomField(String fieldName, String fieldTitle, AbstractField<?> formField, Function<Object, Object> fieldDataProvider, AbstractField<?> tableField) {
		viewFields.add(new RecordVersionViewFieldData(getField(fieldName), fieldName, fieldTitle, formField, fieldDataProvider, tableField, fieldDataProvider));
		return this;
	}

	private FieldIndex getField(String fieldName) {
		return tableIndex.getFieldIndex(fieldName);
	}

	private void createUi() {
		responsiveApplication = ResponsiveApplication.createApplication();
		Perspective perspective = Perspective.createPerspective();
		leftView = perspective.addView(View.createView(StandardLayout.LEFT, ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		centerView = perspective.addView(View.createView(StandardLayout.CENTER, ApplicationIcons.FORM, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		rightView = perspective.addView(View.createView(StandardLayout.RIGHT, ApplicationIcons.TABLE, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		rightView.setVisible(false);
		responsiveApplication.showPerspective(perspective);

		leftView.getPanel().setBodyBackgroundColor(applicationInstanceData.getUser().isDarkTheme() ? Color.DARK_GRAY.withAlpha(0.05f) : Color.WHITE.withAlpha(0.94f));
		centerView.getPanel().setBodyBackgroundColor(applicationInstanceData.getUser().isDarkTheme() ? Color.DARK_GRAY.withAlpha(0.05f) : Color.WHITE.withAlpha(0.94f));

		Template template = createItemTemplate(24, 44, VerticalElementAlignment.CENTER, 48, 1, false);
		PropertyProvider<Integer> propertyProvider = applicationInstanceData.getComponentFactory().createUserTemplateField().getPropertyProvider();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(applicationInstanceData.getUser().getLocale()).withZone(applicationInstanceData.getUser().getSessionContext().getTimeZone());
		PropertyProvider<RecordUpdate> recordUpdatePropertyProvider = (recordUpdate, collection) -> {
			Map<String, Object> values = propertyProvider.getValues(recordUpdate.getUserId(), null);
			Map<String, Object> map = new HashMap<>();
			map.put(BaseTemplate.PROPERTY_ICON, getIcon(recordUpdate));
			map.put(BaseTemplate.PROPERTY_IMAGE, values.get(BaseTemplate.PROPERTY_IMAGE));
			map.put(BaseTemplate.PROPERTY_CAPTION, values.get(BaseTemplate.PROPERTY_CAPTION));
			map.put(BaseTemplate.PROPERTY_DESCRIPTION, values.get(BaseTemplate.PROPERTY_DESCRIPTION));
			map.put(BaseTemplate.PROPERTY_BADGE, dateTimeFormatter.format(Instant.ofEpochMilli(recordUpdate.getTimestamp())));
			return map;
		};

		Set<Integer> viewFieldColumnIds = viewFields.stream().map(f -> f.getFieldIndex().getMappingId()).collect(Collectors.toSet());
		Set<ResolvedTransactionRecordType> fixedUpdateTypes = new HashSet<>(Arrays.asList(ResolvedTransactionRecordType.DELETE, ResolvedTransactionRecordType.RESTORE, ResolvedTransactionRecordType.CREATE, ResolvedTransactionRecordType.CREATE_WITH_ID));
		List<RecordUpdate> filteredRecordUpdates = new ArrayList<>();
		for (RecordUpdate recordUpdate : recordUpdates) {
			if (fixedUpdateTypes.contains(recordUpdate.getRecordType()) || recordUpdate.getRecordValues().stream().anyMatch(recordValue -> viewFieldColumnIds.contains(recordValue.getColumnId()))) {
				filteredRecordUpdates.add(recordUpdate);
			}
		}
		this.recordUpdates = filteredRecordUpdates;

		Table<RecordUpdate> table = new ListTable<>();
		table.setModel(new ListTableModel<>(recordUpdates));
		table.setDisplayAsList(true);
		table.setRowHeight(54);
		table.setForceFitWidth(true);
		TableColumn<RecordUpdate, RecordUpdate> tableColumn = new TableColumn<>("col", "Versions", UiUtils.<RecordUpdate>createTemplateField(template, recordUpdatePropertyProvider));
		table.setHideHeaders(true);
		tableColumn.setValueExtractor(object -> object);
		table.addColumn(tableColumn);

		leftView.setComponent(table);

		ResponsiveForm<?> form = new ResponsiveForm<>(120, 200, 0);
		ResponsiveFormLayout formLayout = form.addResponsiveFormLayout(450);
		formLayout.addSection(ApplicationIcons.EDIT, applicationInstanceData.getLocalized("Changed data")).setHideWhenNoVisibleFields(true);

		Set<Integer> usedColumnIds = recordUpdates.stream()
				.flatMap(rec -> rec.getRecordValues().stream())
				.map(ResolvedTransactionRecordValue::getColumnId)
				.collect(Collectors.toSet());

		List<FieldIndex> metaFields = record.getTableIndex().getFieldIndices().stream().filter(c -> isMetaField(c.getName())).toList();

		List<FieldIndex> sortedColumns = Stream.concat(
				viewFields.stream().map(RecordVersionViewFieldData::getFieldIndex).filter(c -> usedColumnIds.contains(c.getMappingId())),
				metaFields.stream().filter(c -> usedColumnIds.contains(c.getMappingId()))
		).toList();

		List<FieldIndex> columns = Stream.concat(
				metaFields.stream().filter(c -> usedColumnIds.contains(c.getMappingId())),
				viewFields.stream().map(RecordVersionViewFieldData::getFieldIndex).filter(c -> usedColumnIds.contains(c.getMappingId()))
		).toList();


		Map<FieldIndex, RecordVersionViewFieldData> viewFieldByColumn = viewFields.stream().collect(Collectors.toMap(RecordVersionViewFieldData::getFieldIndex, c -> c));

		Map<Integer, AbstractField<?>> fieldMap = new HashMap<>();
		Map<Integer, Function<RecordUpdate, Object>> fieldFunctionMap = new HashMap<>();
		boolean metaSection = false;
		for (FieldIndex column : sortedColumns) {
			boolean metaField = isMetaField(column.getName());
			if (!metaSection && metaField) {
				metaSection = true;
				formLayout.addSection(ApplicationIcons.WINDOW_SIDEBAR, applicationInstanceData.getLocalized(Dictionary.META_DATA)).setHideWhenNoVisibleFields(true);
			}

			Function<RecordUpdate, Object> fieldValueFunction = null;
			AbstractField<?> formField = null;
			String title = null;
			if (metaField) {
				title = getMetaFieldTitle(column.getName());
				formField = createFormField(column);
				fieldValueFunction = createFieldValueFunction(column);
			} else {
				RecordVersionViewFieldData fieldData = viewFieldByColumn.get(column);
				title = fieldData.getFieldTitle();
				if (fieldData.isCustomField()) {
					formField = fieldData.getFormField();
					fieldValueFunction = recordUpdate -> {
						ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
						Object value = updateValue != null ? updateValue.getValue() : null;
						return fieldData.getFormFieldDataProvider().apply(value);
					};
				} else if (fieldData.getReferencedRecordIdToTemplateRecord() != null) {
					formField = createReferenceField(column, false, fieldData.getTemplate());
					fieldValueFunction = createReferenceFieldValueFunction(column, fieldData);
				} else {
					formField = createFormField(column);
					fieldValueFunction = createFieldValueFunction(column);
				}
			}

			if (title == null) {
				title = createTitleFromCamelCase(column.getName());
			}

			fieldFunctionMap.put(column.getMappingId(), fieldValueFunction);
			fieldMap.put(column.getMappingId(), formField);
			formField.setVisible(false);

			if (formField instanceof TagComboBox) {
				formField.setEditingMode(FieldEditingMode.READONLY);
			}
			if (metaSection) {
				formField.setEditingMode(FieldEditingMode.READONLY);
			}
			formLayout.addLabelAndField(null, title, formField);
		}

		table.onSingleRowSelected.addListener(recordUpdate -> {
			Set<AbstractField> fieldSet = new HashSet<>();
			for (ResolvedTransactionRecordValue updateValue : recordUpdate.getRecordValues()) {
				int columnId = updateValue.getColumnId();
				Function<RecordUpdate, Object> fieldValueFunction = fieldFunctionMap.get(columnId);
				if (fieldValueFunction != null) {
					AbstractField field = fieldMap.get(columnId);
					field.setValue(fieldValueFunction.apply(recordUpdate));
					field.setVisible(true);
					fieldSet.add(field);
				}
			}
			fieldMap.values().stream().filter(f -> !fieldSet.contains(f)).forEach(f -> f.setVisible(false));
		});

		centerView.setComponent(form);


		Table<RecordUpdate> versionTable = new ListTable<>();
		versionTable.setModel(new ListTableModel<>(recordUpdates));
//		versionTable.setDisplayAsList(true);
		versionTable.setRowHeight(30);


		for (FieldIndex column : columns) {
			TableColumn<RecordUpdate, ?> tableCol = null;
			String title = null;
			if (isMetaField(column.getName())) {
				tableCol = createTableColumn(column);
				title = getMetaFieldTitle(column.getName());
			} else {
				RecordVersionViewFieldData fieldData = viewFieldByColumn.get(column);
				title = fieldData.getFieldTitle();
				if (fieldData.isCustomField()) {
					tableCol = createCustomTableColumn(column, fieldData);
					tableCol = new TableColumn<RecordUpdate, Object>(column.getName(), fieldData.getTableField()).setValueExtractor(recordUpdate -> {
						ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
						Object value = updateValue != null ? updateValue.getValue() : null;
						return fieldData.getTableFieldDataProvider().apply(value);
					});
					tableCol.setDefaultWidth(fieldData.getTableColumnWidth());

				} else if (fieldData.getReferencedRecordIdToTemplateRecord() != null) {
					Function<RecordUpdate, Object> referenceFieldValueFunction = createReferenceFieldValueFunction(column, fieldData);
					tableCol = new TableColumn<RecordUpdate, Object>(column.getName(), createReferenceField(column, true, null)).setValueExtractor(referenceFieldValueFunction::apply);
				} else {
					tableCol = createTableColumn(column);
				}
			}

			if (title == null) {
				createTitleFromCamelCase(column.getName());
			}
			tableCol.setTitle(title);
			versionTable.addColumn(tableCol);
		}
		rightView.setComponent(versionTable);
	}

	public void showVersionsWindow() {
		showVersionsWindow(false);
	}

	public void showVersionsWindow(boolean showTable) {
		createUi();

		ApplicationWindow window = new ApplicationWindow(ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), applicationInstanceData);
		window.setWindowSize(800, 900);
		window.getWindow().setStretchContent(true);
		window.getWindow().setBodyBackgroundColor(applicationInstanceData.getUser().isDarkTheme() ? Color.DARK_GRAY.withAlpha(0.001f) : Color.WHITE.withAlpha(0.001f));

		window.addOkButton().onClick.addListener(window::close);
		window.addButtonGroup();

		window.setContent(responsiveApplication.getUi());
		window.setWindowRelativeSize(0.7f, 0.7f);
		ToolbarButton versionTableButton = window.addButton(ApplicationIcons.TABLE, "Show as edit table");
		ToolbarButton versionFormButton = window.addButton(ApplicationIcons.FORM, "Show as edit form");
		versionFormButton.setVisible(false);

		versionTableButton.onClick.addListener(() -> {
			versionTableButton.setVisible(false);
			versionFormButton.setVisible(true);
			leftView.setVisible(false);
			centerView.setVisible(false);
			rightView.setVisible(true);
		});

		versionFormButton.onClick.addListener(() -> {
			versionTableButton.setVisible(true);
			versionFormButton.setVisible(false);
			leftView.setVisible(true);
			centerView.setVisible(true);
			rightView.setVisible(false);
		});

		if (showTable) {
			versionTableButton.setVisible(false);
			versionFormButton.setVisible(true);
			leftView.setVisible(false);
			centerView.setVisible(false);
			rightView.setVisible(true);
		}

		window.show();
	}

	private TableColumn<RecordUpdate, ? extends Object> createCustomTableColumn(FieldIndex column, RecordVersionViewFieldData fieldData) {
		TableColumn<RecordUpdate, ? extends Object> tableColumn = new TableColumn<RecordUpdate, Object>(column.getName(), fieldData.getTableField()).setValueExtractor(recordUpdate -> {
			ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
			Object value = updateValue != null ? updateValue.getValue() : null;
			return fieldData.getTableFieldDataProvider().apply(value);
		});
		tableColumn.setDefaultWidth(fieldData.getTableColumnWidth());
		tableColumn.setTitle(fieldData.getFieldTitle());
		return tableColumn;
	}

	private TableColumn<RecordUpdate, ? extends Object> createTableColumn(FieldIndex fieldIndex) {
		String fieldName = fieldIndex.getName();
		TableColumn<RecordUpdate, ? extends Object> tableColumn;
		Function<RecordUpdate, Object> fieldValueFunction = createFieldValueFunction(fieldIndex);
		ValueExtractor<RecordUpdate, Object> valueExtractor = fieldValueFunction::apply;
		Function<RecordUpdate, Object> valueFunction = recordUpdate -> {
			ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(fieldIndex.getMappingId());
			return updateValue != null ? updateValue.getValue() : null;
		};
		if (isMetaUserColumn(fieldIndex)) {
			return new TableColumn<RecordUpdate, Integer>(fieldName, applicationInstanceData.getComponentFactory().createUserTemplateField())
					.setDefaultWidth(250)
					.setValueExtractor(recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (int) value : null;
					});
		} else {
			switch (fieldIndex.getFieldType()) {
				case BOOLEAN -> {
					return new TableColumn<RecordUpdate, Boolean>(fieldName, new CheckBox())
							.setDefaultWidth(70)
							.setValueExtractor(recordUpdate -> {
								Object value = valueFunction.apply(recordUpdate);
								return value != null ? (Boolean) value : null;
							});
				}
				case SHORT, INT, LONG -> {
					return new TableColumn<RecordUpdate, Number>(fieldName, new NumberField(0))
							.setDefaultWidth(70)
							.setValueExtractor(recordUpdate -> {
								Object value = valueFunction.apply(recordUpdate);
								return value != null ? (Number) value : null;
							});
				}
				case FLOAT, DOUBLE -> {
					return new TableColumn<RecordUpdate, Number>(fieldName, new NumberField(2))
							.setDefaultWidth(100)
							.setValueExtractor(recordUpdate -> {
								Object value = valueFunction.apply(recordUpdate);
								return value != null ? (Number) value : null;
							});
				}
				case TEXT -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Object value = valueFunction.apply(recordUpdate);
								return value != null ? (String) value : null;
							});
				}
				case TRANSLATABLE_TEXT -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Object value = valueFunction.apply(recordUpdate);
								return value != null ? ((TranslatableText) value).getText() : null;
							});
				}
				case FILE -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								FileValue value = (FileValue) valueFunction.apply(recordUpdate);
								if (value == null) {
									return null;
								}
								String size = FileUtils.byteCountToDisplaySize(value.getSize());
								return value.getFileName() + " (" + size + ")";
							});
				}
				case SINGLE_REFERENCE -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(250)
							.setValueExtractor(recordUpdate -> (String) valueExtractor.extract(recordUpdate));
				}
				case MULTI_REFERENCE -> {
					TagComboBox<BaseTemplateRecord<Integer>> tagComboBox = new TagComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
					return new TableColumn<RecordUpdate, List<BaseTemplateRecord<Integer>>>(fieldName, tagComboBox)
							.setDefaultWidth(250)
							.setValueExtractor(recordUpdate -> (List<BaseTemplateRecord<Integer>>) valueExtractor.extract(recordUpdate));
				}
				case TIMESTAMP -> {
					return new TableColumn<RecordUpdate, Instant>(fieldName, new InstantDateTimeField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Integer value = (Integer) valueFunction.apply(recordUpdate);
								return value == null ? null : Instant.ofEpochSecond(value);
							});
				}
				case DATE -> {
					return new TableColumn<RecordUpdate, LocalDate>(fieldName, new LocalDateField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Long value = (Long) valueFunction.apply(recordUpdate);
								return value == null ? null : Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalDate();
							});
				}
				case TIME -> {
					return new TableColumn<RecordUpdate, LocalTime>(fieldName, new LocalTimeField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Integer value = (Integer) valueFunction.apply(recordUpdate);
								return value == null ? null : Instant.ofEpochSecond(value).atOffset(ZoneOffset.UTC).toLocalTime();
							});
				}
				case DATE_TIME -> {
					return new TableColumn<RecordUpdate, Instant>(fieldName, new InstantDateTimeField())
							.setDefaultWidth(200)
							.setValueExtractor(recordUpdate -> {
								Long value = (Long) valueFunction.apply(recordUpdate);
								return value == null ? null : Instant.ofEpochMilli(value);
							});
				}
				case LOCAL_DATE -> {
					return new TableColumn<RecordUpdate, LocalDate>(fieldName, new LocalDateField())
							.setDefaultWidth(170)
							.setValueExtractor(recordUpdate -> {
								Long value = (Long) valueFunction.apply(recordUpdate);
								return value == null ? null : Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalDate();
							});
				}
				case ENUM -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(175)
							.setValueExtractor(recordUpdate -> {
								Short value = (Short) valueFunction.apply(recordUpdate);
								EnumFieldModel enumFieldModel = (EnumFieldModel) fieldIndex.getFieldModel().getTableModel().getField(fieldIndex.getName());
								List<String> enumTitles = enumFieldModel.getEnumModel().getEnumTitles();
								return value == null || value == 0 ? null : enumTitles.get(value - 1);
							});
				}
				case BINARY -> {
					return new TableColumn<RecordUpdate, String>(fieldName, new TextField())
							.setDefaultWidth(150)
							.setValueExtractor(recordUpdate -> {
								byte[] value = (byte[]) valueFunction.apply(recordUpdate);
								if (value == null) {
									return null;
								}
								return FileUtils.byteCountToDisplaySize(value.length);
							});
				}
			}
		}
		return null;
	}

	private AbstractField createReferenceField(FieldIndex column, boolean table, Template template) {
		if (column.getFieldType() == FieldType.MULTI_REFERENCE) {
			TagComboBox<BaseTemplateRecord<Integer>> tagComboBox = new TagComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
			if (table) {
				tagComboBox.setWrappingMode(TagBoxWrappingMode.SINGLE_LINE);
			} else {
				tagComboBox.setWrappingMode(TagBoxWrappingMode.SINGLE_TAG_PER_LINE);
				tagComboBox.setTemplate(template != null ? template : BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
			}
			return tagComboBox;
		} else {
			ComboBox<BaseTemplateRecord<Integer>> comboBox = new ComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
			return comboBox;
		}
	}

	private AbstractField<?> createFormField(FieldIndex column) {
		if (isMetaUserColumn(column)) {
			return applicationInstanceData.getComponentFactory().createUserTemplateField();
		} else {
			switch (column.getFieldType()) {
				case BOOLEAN -> {
					return new CheckBox(createTitleFromCamelCase(column.getName()));
				}
				case SHORT, INT, LONG -> {
					return new NumberField(0);
				}
				case FLOAT, DOUBLE -> {
					return new NumberField(2);
				}
				case TEXT -> {
					return new TextField();
				}
				case TRANSLATABLE_TEXT -> {
					return new TextField();
				}
				case FILE -> {
					return new TextField();
				}
				case SINGLE_REFERENCE -> {
					return new TextField();
				}
				case MULTI_REFERENCE -> {
					TagComboBox<BaseTemplateRecord<Integer>> tagComboBox = new TagComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
					tagComboBox.setWrappingMode(TagBoxWrappingMode.SINGLE_TAG_PER_LINE);
					return tagComboBox;
				}
				case TIMESTAMP -> {
					return new InstantDateTimeField();
				}
				case DATE -> {
					return new LocalDateField();
				}
				case TIME -> {
					return new LocalTimeField();
				}
				case DATE_TIME -> {
					return new InstantDateTimeField();
				}
				case LOCAL_DATE -> {
					return new LocalDateField();
				}
				case ENUM -> {
					return new TextField();
				}
				case BINARY -> {
					return new TextField();
				}
			}
		}
		return null;
	}

	private Function<RecordUpdate, Object> createReferenceFieldValueFunction(FieldIndex column, RecordVersionViewFieldData fieldData) {
		Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord = fieldData.getReferencedRecordIdToTemplateRecord();
		if (column.getFieldType() == FieldType.MULTI_REFERENCE) {
			return recordUpdate -> {
				ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
				Object value = updateValue != null ? updateValue.getValue() : null;
				ResolvedMultiReferenceUpdate multiReferenceUpdate = (ResolvedMultiReferenceUpdate) value;
				if (multiReferenceUpdate == null) {
					return null;
				}
				List<BaseTemplateRecord<Integer>> records = new ArrayList<>();
				switch (multiReferenceUpdate.getType()) {
					case ADD_REMOVE_REFERENCES -> {
						for (Integer referencedId : multiReferenceUpdate.getRemoveReferences()) {
							records.add(referencedRecordIdToTemplateRecord.apply(referencedId * -1));
						}
						for (Integer referencedId : multiReferenceUpdate.getAddReferences()) {
							records.add(referencedRecordIdToTemplateRecord.apply(referencedId));
						}
					}
					case SET_REFERENCES -> {
						for (Integer referencedId : multiReferenceUpdate.getSetReferences()) {
							records.add(referencedRecordIdToTemplateRecord.apply(referencedId));
						}
					}
					case REMOVE_ALL_REFERENCES -> records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.ERROR, "Remove all", 0));
				}
				return records;
			};
		} else if (column.getFieldType() == FieldType.SINGLE_REFERENCE) {
			return recordUpdate -> {
				ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
				Object value = updateValue != null ? updateValue.getValue() : null;
				Integer referencedId = (Integer) value;
				if (referencedId == null) {
					return null;
				}
				return referencedRecordIdToTemplateRecord.apply(referencedId);
			};
		}
		return null;
	}

	private Function<RecordUpdate, Object> createFieldValueFunction(FieldIndex fieldIndex) {
		Function<RecordUpdate, Object> valueFunction = recordUpdate -> {
			ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(fieldIndex.getMappingId());
			return updateValue != null ? updateValue.getValue() : null;
		};
		if (isMetaUserColumn(fieldIndex)) {
			return recordUpdate -> {
				Object value = valueFunction.apply(recordUpdate);
				return value != null ? (int) value : null;
			};
		} else {
			switch (fieldIndex.getFieldType()) {
				case BOOLEAN -> {
					return recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (Boolean) value : null;
					};
				}
				case SHORT, INT, LONG -> {
					return recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (Number) value : null;
					};
				}
				case FLOAT, DOUBLE -> {
					return recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (Number) value : null;
					};
				}
				case TEXT -> {
					return recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (String) value : null;
					};
				}
				case TRANSLATABLE_TEXT -> {
					return recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? ((TranslatableText) value).getText() : null;
					};
				}
				case FILE -> {
					return recordUpdate -> {
						FileValue value = (FileValue) valueFunction.apply(recordUpdate);
						if (value == null) {
							return null;
						}
						String size = FileUtils.byteCountToDisplaySize(value.getSize());
						return value.getFileName() + " (" + size + ")";
					};
				}
				case SINGLE_REFERENCE -> {
					return recordUpdate -> {
						Integer value = (Integer) valueFunction.apply(recordUpdate);
						if (value == null) {
							return null;
						}
						SingleReferenceIndex singleReferenceIndex = (SingleReferenceIndex) fieldIndex;
						List<FieldIndex> textIndices = singleReferenceIndex.getReferencedTable().getFieldIndices().stream()
								.filter(c -> c.getFieldType() == FieldType.TEXT || c.getFieldType() == FieldType.TRANSLATABLE_TEXT)
								.limit(5)
								.collect(Collectors.toList());
						return textIndices.stream().map(idx -> idx.getStringValue(value)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
					};
				}
				case MULTI_REFERENCE -> {
					MultiReferenceIndex multiReferenceIndex = (MultiReferenceIndex) fieldIndex;
					List<FieldIndex> textIndices = multiReferenceIndex.getReferencedTable().getFieldIndices().stream()
							.filter(c -> c.getFieldType() == FieldType.TEXT || c.getFieldType() == FieldType.TRANSLATABLE_TEXT)
							.limit(5)
							.collect(Collectors.toList());

					return recordUpdate -> {
						List<BaseTemplateRecord<Integer>> records = new ArrayList<>();
						ResolvedMultiReferenceUpdate multiReferenceUpdate = (ResolvedMultiReferenceUpdate) valueFunction.apply(recordUpdate);
						if (multiReferenceUpdate == null) {
							return null;
						}
						switch (multiReferenceUpdate.getType()) {
							case ADD_REMOVE_REFERENCES -> {
								for (Integer referencedId : multiReferenceUpdate.getRemoveReferences()) {
									String value = textIndices.stream().map(col -> col.getStringValue(referencedId)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
									records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.DELETE, value, referencedId));
								}
								for (Integer referencedId : multiReferenceUpdate.getAddReferences()) {
									String value = textIndices.stream().map(col -> col.getStringValue(referencedId)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
									records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.ADD, value, referencedId));
								}
							}
							case SET_REFERENCES -> {
								for (Integer referencedId : multiReferenceUpdate.getSetReferences()) {
									String value = textIndices.stream().map(col -> col.getStringValue(referencedId)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
									records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.CHECK, value, referencedId));
								}
							}
							case REMOVE_ALL_REFERENCES -> records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.ERROR, "Remove all", 0));
						}
						return records;
					};
				}
				case TIMESTAMP -> {
					return recordUpdate -> {
						Integer value = (Integer) valueFunction.apply(recordUpdate);
						return value == null ? null : Instant.ofEpochSecond(value);
					};
				}
				case DATE -> {
					return recordUpdate -> {
						Long value = (Long) valueFunction.apply(recordUpdate);
						return value == null ? null : Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalDate();
					};
				}
				case TIME -> {
					return recordUpdate -> {
						Integer value = (Integer) valueFunction.apply(recordUpdate);
						return value == null ? null : Instant.ofEpochSecond(value).atOffset(ZoneOffset.UTC).toLocalTime();
					};
				}
				case DATE_TIME -> {
					return recordUpdate -> {
						Long value = (Long) valueFunction.apply(recordUpdate);
						return value == null ? null : Instant.ofEpochMilli(value);
					};
				}
				case LOCAL_DATE -> {
					return recordUpdate -> {
						Long value = (Long) valueFunction.apply(recordUpdate);
						return value == null ? null : Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalDate();
					};
				}
				case ENUM -> {
					return recordUpdate -> {
						Short value = (Short) valueFunction.apply(recordUpdate);
						EnumFieldModel enumFieldModel = (EnumFieldModel) fieldIndex.getFieldModel().getTableModel().getField(fieldIndex.getName());
						List<String> enumTitles = enumFieldModel.getEnumModel().getEnumTitles();
						return value == null || value == 0 ? null : enumTitles.get(value - 1);
					};
				}
				case BINARY -> {
					return recordUpdate -> {
						byte[] value = (byte[]) valueFunction.apply(recordUpdate);
						if (value == null) {
							return null;
						}
						return FileUtils.byteCountToDisplaySize(value.length);
					};
				}
			}
		}
		return null;
	}

	private boolean isMetaUserColumn(FieldIndex<?, ?> FieldIndex) {
		String fieldName = FieldIndex.getName();
		if (FieldIndex.getFieldType() == FieldType.INT && isMetaField(fieldName)) {
			if (
					fieldName.equals(org.teamapps.universaldb.schema.Table.FIELD_CREATED_BY) ||
							fieldName.equals(org.teamapps.universaldb.schema.Table.FIELD_MODIFIED_BY) ||
							fieldName.equals(org.teamapps.universaldb.schema.Table.FIELD_DELETED_BY) ||
							fieldName.equals(org.teamapps.universaldb.schema.Table.FIELD_RESTORED_BY)
			) {
				return true;
			}
		}
		return false;
	}

	private String getMetaFieldTitle(String name) {
		return switch (name) {
			case org.teamapps.universaldb.schema.Table.FIELD_CREATED_BY -> applicationInstanceData.getLocalized(Dictionary.CREATED_BY);
			case org.teamapps.universaldb.schema.Table.FIELD_CREATION_DATE -> applicationInstanceData.getLocalized(Dictionary.CREATION_DATE);
			case org.teamapps.universaldb.schema.Table.FIELD_MODIFIED_BY -> applicationInstanceData.getLocalized(Dictionary.MODIFIED_BY);
			case org.teamapps.universaldb.schema.Table.FIELD_MODIFICATION_DATE -> applicationInstanceData.getLocalized(Dictionary.MODIFICATION_DATE);
			case org.teamapps.universaldb.schema.Table.FIELD_DELETED_BY -> applicationInstanceData.getLocalized(Dictionary.DELETED_BY);
			case org.teamapps.universaldb.schema.Table.FIELD_DELETION_DATE -> applicationInstanceData.getLocalized(Dictionary.DELETION_DATE);
			case org.teamapps.universaldb.schema.Table.FIELD_RESTORED_BY -> applicationInstanceData.getLocalized(Dictionary.RESTORED_BY);
			case org.teamapps.universaldb.schema.Table.FIELD_RESTORE_DATE -> applicationInstanceData.getLocalized(Dictionary.RESTORE_DATE);
			default -> name;
		};
	}

	private boolean isMetaField(int columnId) {
		FieldIndex column = tableIndex.getFieldIndices().stream().filter(col -> col.getMappingId() == columnId).findFirst().orElse(null);
		return isMetaField(column.getName());
	}

	private boolean isMetaField(String fieldName) {
		return org.teamapps.universaldb.schema.Table.isReservedMetaName(fieldName);
	}

	private Icon getIcon(ResolvedMultiReferenceUpdate entry) {
		switch (entry.getType()) {
			case ADD_REMOVE_REFERENCES -> {
				return ApplicationIcons.ADD;
			}
			case SET_REFERENCES -> {
				return ApplicationIcons.CHECK;
			}
			case REMOVE_ALL_REFERENCES -> {
				return ApplicationIcons.ERROR;
			}
		}
		return null;
	}

	private Icon getIcon(RecordUpdate update) {
		switch (update.getRecordType()) {
			case CREATE, CREATE_WITH_ID -> {
				return CompositeIcon.of(ApplicationIcons.DOCUMENT_EMPTY, ApplicationIcons.ADD);
			}
			case UPDATE -> {
				return ApplicationIcons.EDIT;
			}
			case DELETE -> {
				return ApplicationIcons.DELETE;
			}
			case RESTORE -> {
				return ApplicationIcons.GARBAGE_MAKE_EMPTY;
			}
			case ADD_CYCLIC_REFERENCE -> {
				return CompositeIcon.of(ApplicationIcons.GRAPH_CONNECTION_DIRECTED, ApplicationIcons.ADD);
			}
			case REMOVE_CYCLIC_REFERENCE -> {
				return CompositeIcon.of(ApplicationIcons.GRAPH_CONNECTION_DIRECTED, ApplicationIcons.DELETE);
			}
		}
		return null;
	}

	public Template createItemTemplate(int iconSize, int imageSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, int spacing, boolean wrapLines) {
		return new GridTemplate()
				.setAriaLabelProperty(BaseTemplate.PROPERTY_ARIA_LABEL)
				.setTitleProperty(BaseTemplate.PROPERTY_TITLE)
				.setMaxHeight(maxHeight)
				.setPadding(new Spacing(spacing))
				.addColumn(SizingPolicy.AUTO)
				.addColumn(SizingPolicy.AUTO)
				.addColumn(SizingPolicy.FRACTION)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addElement(new IconElement(BaseTemplate.PROPERTY_ICON, 0, 2, iconSize)
						.setRowSpan(3)
						.setVerticalAlignment(verticalIconAlignment)
						.setHorizontalAlignment(HorizontalElementAlignment.RIGHT)
						.setMargin(new Spacing(0, 4, 0, 0)))
				.addElement(new ImageElement(BaseTemplate.PROPERTY_IMAGE, 0, 0, imageSize, imageSize)
						.setRowSpan(3)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(0.5f))
						.setVerticalAlignment(verticalIconAlignment)
						.setMargin(new Spacing(0, 4, 0, 0)))
				.addElement(new TextElement(BaseTemplate.PROPERTY_CAPTION, 0, 1)
						.setWrapLines(wrapLines)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(BaseTemplate.PROPERTY_DESCRIPTION, 1, 1)
						.setColSpan(1)
						.setWrapLines(wrapLines)
						.setFontStyle(0.8f, RgbaColor.GRAY_STANDARD)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(BaseTemplate.PROPERTY_BADGE, 2, 1)
						.setWrapLines(wrapLines)
						.setFontStyle(new FontStyle(1f, RgbaColor.MATERIAL_BLUE_900, null, true, false, false))
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				;
	}
}
