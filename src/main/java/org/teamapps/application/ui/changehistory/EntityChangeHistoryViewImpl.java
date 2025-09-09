package org.teamapps.application.ui.changehistory;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.window.ApplicationWindow;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.icons.Icon;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.reference.multi.MultiReferenceIndex;
import org.teamapps.universaldb.index.reference.single.SingleReferenceIndex;
import org.teamapps.universaldb.index.transaction.resolved.ResolvedTransactionRecordValue;
import org.teamapps.universaldb.index.versioning.RecordUpdate;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.StandardLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.format.*;
import org.teamapps.ux.component.table.ListTable;
import org.teamapps.ux.component.table.TableColumn;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.component.template.gridtemplate.IconElement;
import org.teamapps.ux.component.template.gridtemplate.ImageElement;
import org.teamapps.ux.component.template.gridtemplate.TextElement;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EntityChangeHistoryViewImpl<ENTITY extends Entity<ENTITY>> extends AbstractLazyRenderingApplicationView implements EntityChangeHistoryView<ENTITY> {

	private final EntityChangeHistoryModel<ENTITY> model;
	private final TableIndex tableIndex;
	private final View leftView;
	private final View centerView;
	private final View rightView;
	private ResponsiveApplication responsiveApplication;

	private final List<EntityChangeField> changeFields = new ArrayList<>();

	public static <ENTITY extends Entity<ENTITY>> void showVersionsWindow(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		EntityChangeHistoryViewImpl<ENTITY> changeHistoryView = new EntityChangeHistoryViewImpl<>(EntityChangeHistoryModel.createModelFromEntity(entity), applicationInstanceData);
		ApplicationWindow window = new ApplicationWindow(ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), applicationInstanceData);
		changeHistoryView.setParentWindow(window.getWindow());
		changeHistoryView.show();
	}

	public EntityChangeHistoryViewImpl(EntityChangeHistoryModel<ENTITY> model, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.model = model;
		AbstractUdbEntity<ENTITY> entityBuilder = (AbstractUdbEntity<ENTITY>) model.getEntityBuilder();
		tableIndex = entityBuilder.getTableIndex();

		responsiveApplication = ResponsiveApplication.createApplication();
		Perspective perspective = Perspective.createPerspective();
		leftView = perspective.addView(View.createView(StandardLayout.LEFT, ApplicationIcons.CLOCK_BACK, getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		centerView = perspective.addView(View.createView(StandardLayout.CENTER, ApplicationIcons.FORM, getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		rightView = perspective.addView(View.createView(StandardLayout.RIGHT, ApplicationIcons.TABLE, getLocalized(Dictionary.MODIFICATION_HISTORY), null));
		rightView.setVisible(false);
		leftView.getPanel().setBodyBackgroundColor(Color.WHITE.withAlpha(0.94f));
		centerView.getPanel().setBodyBackgroundColor(Color.WHITE.withAlpha(0.94f));
		responsiveApplication.showPerspective(perspective);

	}

	@Override
	public void addField(String fieldName, String fieldTitle) {
		addChangeField(new EntityChangeField(tableIndex, null, fieldName, fieldTitle, getApplicationInstanceData()));
	}

	@Override
	public void addFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle) {
		EntityChangeField referenceField = getReferenceField(referenceFieldName);
		addChangeField(new EntityChangeField(tableIndex, referenceField, fieldName, fieldTitle, getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addPropertyField(String fieldName, String fieldTitle, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		addChangeField(new EntityChangeField(tableIndex, null, fieldName, fieldTitle, null, null, template, propertyProvider,  getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addPropertyFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		EntityChangeField referenceField = getReferenceField(referenceFieldName);
		addChangeField(new EntityChangeField(tableIndex, referenceField, fieldName, fieldTitle, null, null, template, propertyProvider,  getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addEnumField(String fieldName, String fieldTitle, REFERENCED_ENTITY[] enumValues, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		addChangeField(new EntityChangeField(tableIndex, null, fieldName, fieldTitle, enumValues,null, template, propertyProvider, getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addEnumFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, REFERENCED_ENTITY[] enumValues, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		EntityChangeField referenceField = getReferenceField(referenceFieldName);
		addChangeField(new EntityChangeField(tableIndex, referenceField, fieldName, fieldTitle, enumValues, null, template, propertyProvider, getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addReferenceField(String fieldName, String fieldTitle, EntityBuilder<REFERENCED_ENTITY> entityBuilder, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		addChangeField(new EntityChangeField(tableIndex, null, fieldName, fieldTitle, null, entityBuilder, template, propertyProvider, getApplicationInstanceData()));
	}

	@Override
	public <REFERENCED_ENTITY> void addReferenceFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, EntityBuilder<REFERENCED_ENTITY> entityBuilder, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider) {
		EntityChangeField referenceField = getReferenceField(referenceFieldName);
		addChangeField(new EntityChangeField(tableIndex, referenceField, fieldName, fieldTitle, null, entityBuilder, template, propertyProvider, getApplicationInstanceData()));
	}

	private void addChangeField(EntityChangeField field) {
		if (changeFields.stream().anyMatch(f -> f.getKey().equals(field.getKey()))) {
			throw new IllegalArgumentException("Field " + field.getKey() + " already exists");
		}
		changeFields.add(field);
	}

	private EntityChangeField getReferenceField(String referenceFieldName) {
		EntityChangeField referenceField = changeFields.stream().filter(f -> referenceFieldName.equals(f.getFieldName()) && f.getEntityBuilder() != null).findAny().orElse(null);
		if (referenceField == null) {
			throw new IllegalArgumentException("Field " + referenceFieldName + " doesn't exist");
		}
		return referenceField;
	}

	@Override
	public void createUi() {

		Template template = createItemTemplate(24, 44, VerticalElementAlignment.CENTER, 48, 1, false);


		ListTable<RecordUpdate> table = new ListTable<>();
		table.setDisplayAsList(true);
		table.setRowHeight(54);
		table.setForceFitWidth(true);
		TableColumn<RecordUpdate, RecordUpdate> tableColumn = new TableColumn<>("col", "Versions", UiUtils.createTemplateField(template, createRecordUpdatePropertyProvider()));
		table.setHideHeaders(true);
		tableColumn.setValueExtractor(object -> object);
		table.addColumn(tableColumn);

		leftView.setComponent(table);

		ResponsiveForm<?> form = new ResponsiveForm<>(120, 200, 0);
		ResponsiveFormLayout formLayout = form.addResponsiveFormLayout(450);
		formLayout.addSection(ApplicationIcons.EDIT, getApplicationInstanceData().getLocalized("Changed data")).setHideWhenNoVisibleFields(true);

		for (EntityChangeField changeField : changeFields) {
			changeField.getUiField().getFormField().setEditingMode(FieldEditingMode.READONLY);
			formLayout.addLabelAndField(null, changeField.getFieldLabel(), changeField.getUiField().getFormField());
		}

		leftView.setComponent(table);
		centerView.setComponent(form);

		table.onSingleRowSelected.addListener(recordUpdate -> {
			List<ResolvedTransactionRecordValue> recordValues = recordUpdate.getTransactionRecord().getRecordValues();
			Set<Integer> valueColumnIdSet = recordValues.stream().map(v -> v.getColumnId()).collect(Collectors.toSet());
			for (EntityChangeField changeField : changeFields) {
				boolean contained = valueColumnIdSet.contains(changeField.getColumnId());
				changeField.getUiField().getFormField().setVisible(contained);
				changeField.getUiField().setFieldValue(recordUpdate.getValue(changeField.getColumnId()));
			}
		});

		Set<Integer> usedColumnIds = changeFields.stream().map(EntityChangeField::getColumnId).collect(Collectors.toSet());
		List<EntityChangeField> referencedEntitiesFields = changeFields.stream().map(EntityChangeField::getReferenceField).filter(Objects::nonNull).distinct().toList();

		Consumer<ENTITY> updateHandler = record -> {
			if (record == null || !isVisible()) return;
			AbstractUdbEntity<ENTITY> entity = (AbstractUdbEntity<ENTITY>) record;
			int id = record.getId();
			List<RecordUpdate> allRecordUpdates = new ArrayList<>();
			try {
				List<RecordUpdate> recordUpdates = tableIndex.getRecordVersioningIndex().readRecordUpdates(id);
				allRecordUpdates.addAll(recordUpdates);
				for (EntityChangeField changeField : referencedEntitiesFields) {
					if (changeField.isMultiReferenceField()) {
						MultiReferenceIndex multiReferenceIndex = (MultiReferenceIndex) changeField.getFieldIndex();
						List<RecordUpdate> refUpdates = multiReferenceIndex.getReferencedTable().getRecordVersioningIndex().readRecordUpdates(id);
						allRecordUpdates.addAll(refUpdates);
					} else if (changeField.isReferenceField()) {
						SingleReferenceIndex singleReferenceIndex = (SingleReferenceIndex) changeField.getFieldIndex();
						int refValue = singleReferenceIndex.getValue(id);
						List<RecordUpdate> refUpdates = singleReferenceIndex.getReferencedTable().getRecordVersioningIndex().readRecordUpdates(refValue);
						allRecordUpdates.addAll(refUpdates);
					}
				}

				List<RecordUpdate> sortedRecords = allRecordUpdates.stream()
						.filter(recordUpdate -> recordUpdate.getRecordValues().stream().anyMatch(v -> usedColumnIds.contains(v.getColumnId())))
						.sorted(Comparator.comparingLong(RecordUpdate::getTransactionId)).toList();

				table.setRecords(sortedRecords);
				leftView.getPanel().setTitle(getLocalized(Dictionary.MODIFICATION_HISTORY) + " (" + sortedRecords.size() + ")");

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};

		model.getSelectedEntity().onChanged().addListener(record -> {
			if (isVisible()) {
				updateHandler.accept(record);
			}
		});
		updateHandler.accept(model.getSelectedEntity().get());
		onViewRedrawRequired.addListener(() -> {
			ENTITY entity = model.getSelectedEntity().get();
			updateHandler.accept(entity);
		});

	}

	private PropertyProvider<RecordUpdate> createRecordUpdatePropertyProvider() {
		PropertyProvider<Integer> propertyProvider = getComponentFactory().createUserTemplateField().getPropertyProvider();
		return (recordUpdate, collection) -> {
			Map<String, Object> map = propertyProvider.getValues(recordUpdate.getUserId(), null);
			map.put(BaseTemplate.PROPERTY_ICON, getRecordUpdateIcon(recordUpdate));
			map.put(BaseTemplate.PROPERTY_BADGE, getLocalizedFormatter().formatDateTime(Instant.ofEpochMilli(recordUpdate.getTimestamp())));
			return map;
		};
	}

	private Icon getRecordUpdateIcon(RecordUpdate update) {
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

	@Override
	public Component getViewComponent() {
		return responsiveApplication.getUi();
	}

}
