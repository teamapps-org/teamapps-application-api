package org.teamapps.application.ui.changehistory;

import org.apache.commons.io.FileUtils;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.universaldb.index.file.FileValue;
import org.teamapps.universaldb.index.reference.value.ResolvedMultiReferenceType;
import org.teamapps.universaldb.index.reference.value.ResolvedMultiReferenceUpdate;
import org.teamapps.universaldb.index.transaction.resolved.ResolvedTransactionRecordValue;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.universaldb.index.versioning.RecordUpdate;
import org.teamapps.universaldb.model.FieldType;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.field.*;
import org.teamapps.ux.component.field.combobox.TagBoxWrappingMode;
import org.teamapps.ux.component.field.combobox.TagComboBox;
import org.teamapps.ux.component.field.datetime.InstantDateTimeField;
import org.teamapps.ux.component.field.datetime.LocalDateField;
import org.teamapps.ux.component.field.datetime.LocalTimeField;
import org.teamapps.ux.component.table.TableColumn;
import org.teamapps.ux.component.template.Template;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;

public class EntityChangeUiField {

	private final EntityChangeField parentField;
	private final EntityBuilder entityBuilder;
	private final PropertyProvider propertyProvider;
	private final Template template;

	private final AbstractField formField;
	private final TableColumn<RecordUpdate, ?> tableColumn;

	private MultiReferenceUpdateType multiReferenceUpdateType;

	public static enum MultiReferenceUpdateType {
		ADD,
		REMOVE,
		SET,
		REMOVE_ALL
	}

	public EntityChangeUiField(EntityChangeField parentField) {
		this.parentField = parentField;
		this.entityBuilder = null;
		this.propertyProvider = null;
		this.template = null;
		this.formField = createUiField(false, parentField.getApplicationInstanceData());
		this.tableColumn = createTableColumn(parentField.getApplicationInstanceData());
	}

	public EntityChangeUiField(EntityChangeField parentField, EntityBuilder entityBuilder, PropertyProvider propertyProvider, Template template, ApplicationInstanceData applicationInstanceData) {
		this.parentField = parentField;
		this.entityBuilder = entityBuilder;
		this.propertyProvider = propertyProvider;
		this.template = template;
		this.formField = createUiField(false, applicationInstanceData);
		this.tableColumn = createTableColumn(applicationInstanceData);
	}

	private AbstractField createUiField(boolean tableField, ApplicationInstanceData applicationInstanceData) {
		String fieldName = parentField.getFieldName();
		if (parentField.isMetaField()) {
			if (fieldName.equals(Table.FIELD_CREATED_BY) || fieldName.equals(Table.FIELD_MODIFIED_BY) || fieldName.equals(Table.FIELD_RESTORED_BY) || fieldName.equals(Table.FIELD_DELETED_BY)) {
				return applicationInstanceData.getComponentFactory().createUserTemplateField();
			} else if (fieldName.equals(Table.FIELD_CREATION_DATE) || fieldName.equals(Table.FIELD_MODIFICATION_DATE) || fieldName.equals(Table.FIELD_RESTORE_DATE) || fieldName.equals(Table.FIELD_DELETION_DATE)) {
				return new InstantDateTimeField();
			}
		} else {
			FieldType fieldType = parentField.getFieldIndex().getFieldType();
			if (propertyProvider != null && fieldType == FieldType.TEXT || fieldType == FieldType.INT) {
				return UiUtils.createTemplateField(template, propertyProvider);
			}

			switch (parentField.getFieldIndex().getFieldType()) {
				case BOOLEAN -> {
					return new CheckBox(parentField.getFieldTitle());
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
					return new TranslatableViewField(applicationInstanceData);
				}
				case FILE, BINARY -> {
					return new DisplayField(false, true);
				}
				case SINGLE_REFERENCE, ENUM -> {
					return UiUtils.createTemplateField(template, propertyProvider);
				}
				case MULTI_REFERENCE -> {
					TagComboBox<Object> tagComboBox = new TagComboBox<>(template);
					tagComboBox.setWrappingMode(TagBoxWrappingMode.SINGLE_TAG_PER_LINE);
					tagComboBox.setShowClearButton(false);
					tagComboBox.setShowDropDownAfterResultsArrive(true);
					tagComboBox.setDropDownButtonVisible(false);
					tagComboBox.setPropertyProvider(propertyProvider);
					return tagComboBox;
				}
				case TIMESTAMP, DATE_TIME -> {
					return new InstantDateTimeField();
				}
				case DATE, LOCAL_DATE -> {
					return new LocalDateField();
				}
				case TIME -> {
					return new LocalTimeField();
				}
			}
		}
		return null;
	}

	public TableColumn<RecordUpdate, ? extends Object> createTableColumn(ApplicationInstanceData applicationInstanceData) {
		AbstractField field = createUiField(true, applicationInstanceData);
		TableColumn<RecordUpdate, Object> column = new TableColumn<>(parentField.getKey(), null, parentField.getFieldTitle(), field);
		column.setValueExtractor(this::getFieldValue);
		return column;
	}

	public void setFieldValue(ResolvedTransactionRecordValue transactionRecordValue) {
		Object fieldValue = getFieldValue(transactionRecordValue);
		formField.setValue(fieldValue);
	}

	private Object getFieldValue(RecordUpdate recordUpdate) {
		ResolvedTransactionRecordValue transactionRecordValue = recordUpdate.getValue(parentField.getColumnId());
		return transactionRecordValue != null ? getFieldValue(transactionRecordValue) : null;
	}

	private Object getFieldValue(ResolvedTransactionRecordValue transactionRecordValue) {
		if (transactionRecordValue == null) {
			return null;
		}
		Object value = transactionRecordValue.getValue();
		String fieldName = parentField.getFieldName();
		if (parentField.isMetaField()) {
			if (fieldName.equals(Table.FIELD_CREATED_BY) || fieldName.equals(Table.FIELD_MODIFIED_BY) || fieldName.equals(Table.FIELD_RESTORED_BY) || fieldName.equals(Table.FIELD_DELETED_BY)) {
				return value;
			} else if (fieldName.equals(Table.FIELD_CREATION_DATE) || fieldName.equals(Table.FIELD_MODIFICATION_DATE) || fieldName.equals(Table.FIELD_RESTORE_DATE) || fieldName.equals(Table.FIELD_DELETION_DATE)) {
				return Instant.ofEpochSecond((int) value);
			}
		} else {
			FieldType fieldType = parentField.getFieldIndex().getFieldType();
			if (propertyProvider != null && fieldType == FieldType.TEXT || fieldType == FieldType.INT) {
				return value;
			}
			switch (fieldType) {
				case BOOLEAN -> {
					return value != null && (boolean) value;
				}
				case SHORT, INT, LONG, FLOAT, DOUBLE -> {
					return value;
				}
				case TEXT -> {
					return value;
				}
				case TRANSLATABLE_TEXT -> {
					return value != null ? new TranslatableText((String) value) : null;
				}
				case FILE -> {
					FileValue fileValue = (FileValue) value;
					if (fileValue != null) {
						String fileName = fileValue.getFileName();
						String formattedSize = FileUtils.byteCountToDisplaySize(fileValue.getSize());
						return fileName + " (" + formattedSize + ")";
					} else {
						return null;
					}
				}
				case SINGLE_REFERENCE -> {
					if (value != null) {
						return entityBuilder.build((int) value);
					} else {
						return null;
					}
				}
				case MULTI_REFERENCE -> {
					ResolvedMultiReferenceUpdate multiReferenceUpdate = (ResolvedMultiReferenceUpdate) value;
					if (multiReferenceUpdate.getType() == ResolvedMultiReferenceType.ADD_REMOVE_REFERENCES) {
						if (multiReferenceUpdate.getAddReferences() != null && !multiReferenceUpdate.getAddReferences().isEmpty()) {
							return multiReferenceUpdate.getAddReferences().stream().map(entityBuilder::build).toList();
						} else {
							return multiReferenceUpdate.getRemoveReferences().stream().map(entityBuilder::build).toList();
						}
						//						if (multiReferenceUpdateType == MultiReferenceUpdateType.ADD) {
						//
						//						} else if (multiReferenceUpdateType == MultiReferenceUpdateType.REMOVE) {
						//
						//						}
					} else if (multiReferenceUpdate.getType() == ResolvedMultiReferenceType.SET_REFERENCES) {
						return multiReferenceUpdate.getSetReferences().stream().map(entityBuilder::build).toList();
						//						if (multiReferenceUpdateType == MultiReferenceUpdateType.SET) {
						//
						//						}
					} else if (multiReferenceUpdate.getType() == ResolvedMultiReferenceType.REMOVE_ALL_REFERENCES) {
						return Collections.emptyList();
						//						if (multiReferenceUpdateType == MultiReferenceUpdateType.REMOVE_ALL) {
						//
						//						}
					}

				}
				case TIMESTAMP -> {
					return value != null ? Instant.ofEpochSecond((int) value) : null;
				}
				case DATE, LOCAL_DATE -> {
					return value != null ? LocalDate.ofInstant(Instant.ofEpochMilli((long) value), ZoneOffset.UTC) : null;
				}
				case TIME -> {
					return value != null ? LocalTime.ofSecondOfDay((int) value) : null;
				}
				case DATE_TIME -> {
					return value != null ? Instant.ofEpochMilli((long) value) : null;
				}
				case ENUM -> {
					return value != null ? parentField.getEnumValues()[(short) value - 1] : null;
				}
				case BINARY -> {
					return value != null ? FileUtils.byteCountToDisplaySize(((byte[]) value).length) : null;
				}
			}
		}
		return null;
	}

public AbstractField getFormField() {
	return formField;
}

public TableColumn<RecordUpdate, ?> getTableColumn() {
	return tableColumn;
}

public MultiReferenceUpdateType getMultiReferenceUpdateType() {
	return multiReferenceUpdateType;
}
}
