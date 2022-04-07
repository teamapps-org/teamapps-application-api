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
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.index.ColumnIndex;
import org.teamapps.universaldb.index.ColumnType;
import org.teamapps.universaldb.index.file.FileValue;
import org.teamapps.universaldb.index.reference.multi.MultiReferenceIndex;
import org.teamapps.universaldb.index.reference.single.SingleReferenceIndex;
import org.teamapps.universaldb.index.reference.value.ResolvedMultiReferenceUpdate;
import org.teamapps.universaldb.index.transaction.resolved.ResolvedTransactionRecordValue;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.universaldb.index.versioning.RecordUpdate;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.StandardLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.field.*;
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
	private ResponsiveApplication responsiveApplication;
	private View leftView;
	private View centerView;
	private View rightView;

	public RecordVersionsView(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		this.entity = entity;
		this.applicationInstanceData = applicationInstanceData;
		createUi();
	}

	private void createUi() {
		AbstractUdbEntity<ENTITY> record = (AbstractUdbEntity<ENTITY>) entity;
		List<RecordUpdate> recordUpdates = record.getRecordUpdates();

		responsiveApplication = ResponsiveApplication.createApplication();
		Perspective perspective = Perspective.createPerspective();
		leftView = perspective.addView(View.createView(StandardLayout.LEFT, ApplicationIcons.INDEX, "Versions", null));
		centerView = perspective.addView(View.createView(StandardLayout.CENTER, ApplicationIcons.FORM, "Edited Fields", null));
		rightView = perspective.addView(View.createView(StandardLayout.RIGHT, ApplicationIcons.TABLE, "Version Table", null));
		rightView.setVisible(false);
		responsiveApplication.showPerspective(perspective);

		leftView.getPanel().setBodyBackgroundColor(Color.WHITE.withAlpha(0.94f));
		centerView.getPanel().setBodyBackgroundColor(Color.WHITE.withAlpha(0.94f));

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
			map.put(BaseTemplate.PROPERTY_BADGE, dateTimeFormatter.format(Instant.ofEpochMilli(recordUpdate.getTimestamp() * 1000L)));
			return map;
		};

		Table<RecordUpdate> table = new ListTable<>();
		table.setModel(new ListTableModel<>(recordUpdates));
		table.setDisplayAsList(true);
		table.setRowHeight(54);
		table.setForceFitWidth(true);
		TableColumn<RecordUpdate, RecordUpdate> tableColumn = new TableColumn<>("col", "Versions", UiUtils.<RecordUpdate>createTemplateField(template, recordUpdatePropertyProvider));
		tableColumn.setValueExtractor(object -> object);
		table.addColumn(tableColumn);

		leftView.setComponent(table);

		ResponsiveForm<?> form = new ResponsiveForm<>(120, 200, 0);
		ResponsiveFormLayout formLayout = form.addResponsiveFormLayout(450);
		formLayout.addSection(ApplicationIcons.EDIT, applicationInstanceData.getLocalized("Changed data")).setHideWhenNoVisibleFields(true);


		Set<Integer> usedColumnIds = recordUpdates.stream().flatMap(rec -> rec.getRecordValues().stream()).map(ResolvedTransactionRecordValue::getColumnId).collect(Collectors.toSet());
		List<ColumnIndex> columns = record.getTableIndex().getColumnIndices().stream().filter(col -> usedColumnIds.contains(col.getMappingId())).collect(Collectors.toList());
		List<ColumnIndex> sortedColumns = Stream.concat(columns.stream().filter(c -> !isMetaField(c.getName())), columns.stream().filter(c -> isMetaField(c.getName()))).collect(Collectors.toList());

		Map<Integer, AbstractField<?>> fieldMap = new HashMap<>();
		Map<Integer, Function<RecordUpdate, Object>> fieldFunctionMap = new HashMap<>();
		boolean metaSection = false;
		for (ColumnIndex column : sortedColumns) {
			if (!metaSection && isMetaField(column.getName())) {
				metaSection = true;
				formLayout.addSection(ApplicationIcons.WINDOW_SIDEBAR, applicationInstanceData.getLocalized(Dictionary.META_DATA)).setHideWhenNoVisibleFields(true);
			}
			Function<RecordUpdate, Object> fieldValueFunction = createFieldValueFunction(column);
			fieldFunctionMap.put(column.getMappingId(), fieldValueFunction);
			AbstractField<?> formField = createFormField(column);
			fieldMap.put(column.getMappingId(), formField);
			formField.setVisible(false);
			if (!(formField instanceof TextField || formField instanceof NumberField)) {
				//formField.setEditingMode(FieldEditingMode.READONLY);
			}
			if (formField instanceof TagComboBox) {
				formField.setEditingMode(FieldEditingMode.READONLY);
			}
			if (metaSection) {
				formField.setEditingMode(FieldEditingMode.READONLY);
			}
			formLayout.addLabelAndField(null, createTitleFromCamelCase(column.getName()), formField);
		}

		table.onSingleRowSelected.addListener(recordUpdate -> {
			Set<AbstractField> fieldSet = new HashSet<>();
			for (ResolvedTransactionRecordValue updateValue : recordUpdate.getRecordValues()) {
				int columnId = updateValue.getColumnId();
				Function<RecordUpdate, Object> fieldValueFunction = fieldFunctionMap.get(columnId);
				AbstractField field = fieldMap.get(columnId);
				field.setValue(fieldValueFunction.apply(recordUpdate));
				field.setVisible(true);
				fieldSet.add(field);
			}
			fieldMap.values().stream().filter(f -> !fieldSet.contains(f)).forEach(f -> f.setVisible(false));
		});

		centerView.setComponent(form);


		Table<RecordUpdate> versionTable = new ListTable<>();
		versionTable.setModel(new ListTableModel<>(recordUpdates));
//		versionTable.setDisplayAsList(true);
		versionTable.setRowHeight(30);


		for (ColumnIndex column : columns) {
			TableColumn<RecordUpdate, ?> tableCol = createTableColumn(column);
			tableCol.setTitle(createTitleFromCamelCase(column.getName()));
			versionTable.addColumn(tableCol);
		}
		rightView.setComponent(versionTable);
	}


	public void showVersionsWindow() {
		ApplicationWindow window = new ApplicationWindow(ApplicationIcons.INDEX, "Record versions", applicationInstanceData);
		window.getWindow().setBodyBackgroundColor(Color.WHITE.withAlpha(0.001f));

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

		window.addCancelButton();
		window.show();
	}

	private TableColumn<RecordUpdate, ? extends Object> createTableColumn(ColumnIndex column) {
		String fieldName = column.getName();
		TableColumn<RecordUpdate, ? extends Object> tableColumn;
		Function<RecordUpdate, Object> valueFunction = recordUpdate -> {
			ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
			return updateValue != null ? updateValue.getValue() : null;
		};
		if (isMetaUserColumn(column)) {
			return new TableColumn<RecordUpdate, Integer>(fieldName, applicationInstanceData.getComponentFactory().createUserTemplateField())
					.setDefaultWidth(250)
					.setValueExtractor(recordUpdate -> {
						Object value = valueFunction.apply(recordUpdate);
						return value != null ? (int) value : null;
					});
		} else {
			switch (column.getColumnType()) {
				case BOOLEAN, BITSET_BOOLEAN -> {
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
							.setValueExtractor(recordUpdate -> {
								Integer value = (Integer) valueFunction.apply(recordUpdate);
								if (value == null) {
									return null;
								}
								SingleReferenceIndex singleReferenceIndex = (SingleReferenceIndex) column;
								List<ColumnIndex> textIndices = singleReferenceIndex.getReferencedTable().getColumnIndices().stream()
										.filter(c -> c.getColumnType() == ColumnType.TEXT || c.getColumnType() == ColumnType.TRANSLATABLE_TEXT)
										.limit(5)
										.collect(Collectors.toList());
								return textIndices.stream().map(idx -> idx.getStringValue(value)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
							});
				}
				case MULTI_REFERENCE -> {
					TagComboBox<BaseTemplateRecord<Integer>> tagComboBox = new TagComboBox<>(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
					return new TableColumn<RecordUpdate, List<BaseTemplateRecord<Integer>>>(fieldName, tagComboBox)
							.setDefaultWidth(250)
							.setValueExtractor(recordUpdate -> (List<BaseTemplateRecord<Integer>>) valueFunction.apply(recordUpdate));
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
								List<String> enumValues = column.getTable().getTable().getColumn(column.getName()).getEnumValues();
								return value == null || value == 0 ? null : enumValues.get(value - 1);
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

	private AbstractField<?> createFormField(ColumnIndex column) {
		if (isMetaUserColumn(column)) {
			return applicationInstanceData.getComponentFactory().createUserTemplateField();
		} else {
			switch (column.getColumnType()) {
				case BOOLEAN, BITSET_BOOLEAN -> {
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

	private Function<RecordUpdate, Object> createFieldValueFunction(ColumnIndex column) {
		Function<RecordUpdate, Object> valueFunction = recordUpdate -> {
			ResolvedTransactionRecordValue updateValue = recordUpdate.getValue(column.getMappingId());
			return updateValue != null ? updateValue.getValue() : null;
		};
		if (isMetaUserColumn(column)) {
			return recordUpdate -> {
				Object value = valueFunction.apply(recordUpdate);
				return value != null ? (int) value : null;
			};
		} else {
			switch (column.getColumnType()) {
				case BOOLEAN, BITSET_BOOLEAN -> {
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
						SingleReferenceIndex singleReferenceIndex = (SingleReferenceIndex) column;
						List<ColumnIndex> textIndices = singleReferenceIndex.getReferencedTable().getColumnIndices().stream()
								.filter(c -> c.getColumnType() == ColumnType.TEXT || c.getColumnType() == ColumnType.TRANSLATABLE_TEXT)
								.limit(5)
								.collect(Collectors.toList());
						return textIndices.stream().map(idx -> idx.getStringValue(value)).filter(s -> !"NULL".equals(s)).filter(Objects::nonNull).collect(Collectors.joining(" "));
					};
				}
				case MULTI_REFERENCE -> {
					MultiReferenceIndex multiReferenceIndex = (MultiReferenceIndex) column;
					List<ColumnIndex> textIndices = multiReferenceIndex.getReferencedTable().getColumnIndices().stream()
							.filter(c -> c.getColumnType() == ColumnType.TEXT || c.getColumnType() == ColumnType.TRANSLATABLE_TEXT)
							.limit(5)
							.collect(Collectors.toList());

					return recordUpdate -> {
						List<BaseTemplateRecord<Integer>> records = new ArrayList<>();
						ResolvedMultiReferenceUpdate multiReferenceUpdate = (ResolvedMultiReferenceUpdate) valueFunction.apply(recordUpdate);
						switch (multiReferenceUpdate.getType()) {
							case REMOVE_ALL_REFERENCES -> {
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
							case ADD_REMOVE_REFERENCES -> records.add(new BaseTemplateRecord<Integer>(ApplicationIcons.ERROR, "Remove all", 0));
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
						List<String> enumValues = column.getTable().getTable().getColumn(column.getName()).getEnumValues();
						return value == null || value == 0 ? null : enumValues.get(value - 1);
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

	private boolean isMetaUserColumn(ColumnIndex<?, ?> columnIndex) {
		String fieldName = columnIndex.getName();
		if (columnIndex.getColumnType() == ColumnType.INT && isMetaField(fieldName)) {
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
			case CREATE -> {
				return ApplicationIcons.ADD;
			}
			case UPDATE -> {
				return ApplicationIcons.EDIT;
			}
			case DELETE -> {
				return ApplicationIcons.DELETE;
			}
			case RESTORE -> {
				return ApplicationIcons.CLOCK_BACK;
			}
			case ADD_CYCLIC_REFERENCE -> {
				return ApplicationIcons.ADD;
			}
			case REMOVE_CYCLIC_REFERENCE -> {
				return ApplicationIcons.DELETE;
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
