package org.teamapps.application.ui.changehistory;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.universaldb.index.FieldIndex;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.reference.multi.MultiReferenceIndex;
import org.teamapps.universaldb.index.reference.single.SingleReferenceIndex;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.template.Template;

import java.util.List;

public class EntityChangeField {

	private final TableIndex tableIndex;
	private final int tableId;
	private final FieldIndex<?, ?> fieldIndex;
	private final String fieldName;
	private final int columnId;
	private final String fieldTitle;
	private final Object[] enumValues;
	private final EntityBuilder<?> entityBuilder;
	private final Template template;
	private final PropertyProvider<?> propertyProvider;
	private final EntityChangeField referenceField;
	private final ApplicationInstanceData applicationInstanceData;

	private EntityChangeUiField uiField;
	private List<EntityChangeUiField> multiRefFields;


	public EntityChangeField(TableIndex baseTableIndex, EntityChangeField referenceField, String fieldName, String fieldTitle, ApplicationInstanceData applicationInstanceData) {
		TableIndex tableIndex = baseTableIndex;
		if (referenceField != null && referenceField.isReferenceField()) {
			if (referenceField.isMultiReferenceField()) {
				tableIndex = ((MultiReferenceIndex) referenceField.getFieldIndex()).getReferencedTable();
			} else {
				tableIndex = ((SingleReferenceIndex) referenceField.getFieldIndex()).getReferencedTable();
			}
		}
		this.tableIndex = tableIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.tableId = tableIndex.getMappingId();
		this.applicationInstanceData = applicationInstanceData;
		this.fieldIndex = tableIndex.getFieldIndex(fieldName);
		this.columnId = fieldIndex.getMappingId();
		this.enumValues = null;
		this.entityBuilder = null;
		this.template = null;
		this.propertyProvider = null;
		this.referenceField = referenceField;
		if (isReferenceField()) {
			throw new RuntimeException("Reference fields must be configured with entity builder and property provider - field: " + fieldIndex.getFQN());
		}
		if (isEnumField()) {
			throw new RuntimeException("Enum fields must be configured with a property provider - field: " + fieldIndex.getFQN());
		}
		this.uiField = new EntityChangeUiField(this);
	}

	public EntityChangeField(TableIndex baseTableIndex, EntityChangeField referenceField, String fieldName, String fieldTitle, Object[] enumValues, EntityBuilder<?> entityBuilder, Template template, PropertyProvider<?> propertyProvider, ApplicationInstanceData applicationInstanceData) {
		this.tableIndex = baseTableIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.tableId = baseTableIndex.getMappingId();
		this.applicationInstanceData = applicationInstanceData;
		this.fieldIndex = baseTableIndex.getFieldIndex(fieldName);
		this.columnId = fieldIndex.getMappingId();
		this.enumValues = enumValues;
		this.entityBuilder = entityBuilder;
		this.template = template;
		this.propertyProvider = propertyProvider;
		this.referenceField = referenceField;
		this.uiField = new EntityChangeUiField(this, entityBuilder, propertyProvider, template, getApplicationInstanceData());
	}

	public boolean isReferenceField() {
		return fieldIndex.getFieldType().isReference();
	}

	public boolean isMultiReferenceField() {
		return fieldIndex.getFieldType().isMultiReference();
	}

	public boolean isEnumField() {
		return fieldIndex.getFieldType().isEnum();
	}

	public String getKey() {
		return referenceField != null ? referenceField.getFieldName() + "." + fieldName : fieldName;
	}

	public String getFieldLabel() {
		return referenceField != null ? referenceField.getFieldTitle() + " → " + fieldTitle : fieldTitle;
	}

	public boolean isMetaField() {
		return Table.isReservedMetaName(fieldName);
	}

	public TableIndex getTableIndex() {
		return tableIndex;
	}

	public int getTableId() {
		return tableId;
	}

	public FieldIndex<?, ?> getFieldIndex() {
		return fieldIndex;
	}

	public String getFieldName() {
		return fieldName;
	}

	public int getColumnId() {
		return columnId;
	}

	public String getFieldTitle() {
		return fieldTitle;
	}

	public Object[] getEnumValues() {
		return enumValues;
	}

	public EntityBuilder<?> getEntityBuilder() {
		return entityBuilder;
	}

	public Template getTemplate() {
		return template;
	}

	public PropertyProvider<?> getPropertyProvider() {
		return propertyProvider;
	}

	public EntityChangeField getReferenceField() {
		return referenceField;
	}

	public EntityChangeUiField getUiField() {
		return uiField;
	}

	public List<EntityChangeUiField> getMultiRefFields() {
		return multiRefFields;
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}


}
