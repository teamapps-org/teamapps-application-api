package org.teamapps.application.ui.table;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.AbstractPerspectiveModel;
import org.teamapps.data.value.SortDirection;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormConfigurationTemplate;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.format.SizeType;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.table.ListTable;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractEntityTableFilterView<ENTITY extends Entity<ENTITY>> extends AbstractLazyRenderingApplicationView  {


	private final AbstractPerspectiveModel<ENTITY> perspectiveModel;
	private boolean initialised;
	private VerticalLayout verticalLayout;
	private ListTable<ENTITY> table;
	private List<FilterField> filterFields = new ArrayList<>();

	private record FilterField(AbstractField<?> field, String title, Icon<?, ?> icon) {}

	public AbstractEntityTableFilterView(AbstractPerspectiveModel<ENTITY> perspectiveModel, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.perspectiveModel = perspectiveModel;

		onViewRedrawRequired.addListener(() -> {
			if (!initialised) {
				initialised = true;

			}
		});
	}

	@Override
	public void createUi() {
		verticalLayout = new VerticalLayout();
		ResponsiveFormConfigurationTemplate filterTemplate = ResponsiveFormConfigurationTemplate.createDefaultTwoColumnTemplate(0, 20, 500);
		filterTemplate.setColumnTemplateByColumn(1, new GridColumn(new SizingPolicy(SizeType.FRACTION, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(2, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(3, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(4, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(5, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(6, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		filterTemplate.setColumnTemplateByColumn(7, new GridColumn(new SizingPolicy(SizeType.AUTO, 1.0F, 0), 0, 10));
		ResponsiveForm<Void> filterForm = new ResponsiveForm<>(filterTemplate);

		ResponsiveFormLayout selectionLayoutLarge = filterForm.addResponsiveFormLayout(550);
		selectionLayoutLarge.addSection().setCollapsible(false).setDrawHeaderLine(false);
		ResponsiveFormLayout selectionLayoutMiddle = filterForm.addResponsiveFormLayout(420);
		selectionLayoutMiddle.addSection().setCollapsible(false).setDrawHeaderLine(false);
		ResponsiveFormLayout selectionLayoutTiny = filterForm.addResponsiveFormLayout(240);
		selectionLayoutTiny.addSection().setCollapsible(false).setDrawHeaderLine(false);

		boolean odd = true;
		for (FilterField filterField : filterFields) {
			if (odd) {
				selectionLayoutLarge.addLabelAndField(filterField.icon, filterField.title, filterField.field);
				selectionLayoutMiddle.addLabelAndField(filterField.icon, "", filterField.field);
				selectionLayoutTiny.addLabelAndField(null, "", filterField.field);
			} else {
				selectionLayoutLarge.addLabelAndField(filterField.icon, filterField.title, filterField.field, false);
				selectionLayoutMiddle.addLabelAndField(filterField.icon, "", filterField.field, false);
				selectionLayoutTiny.addLabelAndField(null, "", filterField.field, false);
			}
			odd = !odd;
		}


		verticalLayout.addComponent(filterForm);
		table = new  ListTable<>();
		table.setDisplayAsList(true);
		table.setRowHeight(28);
		verticalLayout.addComponentFillRemaining(table);

		String baseTitle = getParentPanel().getTitle();
		getParentPanel().setTitle((perspectiveModel.getShowDeletedEntities().get() ? "🗑" + getLocalized("apps.deleted") + ": " : "") + baseTitle + " (" + getLocalizedFormatter().formatDecimalNumber(Optional.ofNullable(perspectiveModel.getEntityCount()).map(ObservableValue::get).orElse(0))+")");
		perspectiveModel.getEntityCount().onChanged().addListener(entityCount -> getParentPanel().setTitle((perspectiveModel.getShowDeletedEntities().get() ? "🗑" + getLocalized("apps.deleted") + ": " : "") + baseTitle + " (" + getLocalizedFormatter().formatDecimalNumber(entityCount)+")"));
		perspectiveModel.setFullTextFilterField(getParentPanel());
		onViewRedrawRequired.addListener(() -> table.setRecords(perspectiveModel.getEntities().get()));
		perspectiveModel.getEntities().onChanged().addListener(entities -> {
			if (isVisible()) {
				table.setRecords(entities);
			}
		});
		perspectiveModel.getSelectedEntity().onChanged().addListener(entity -> {
			if (!Objects.equals(entity, table.getSelectedRecord())) {
				table.setSelectedRecord(entity);
			}
		});
		table.onSingleRowSelected.addListener(record -> {
			perspectiveModel.handleEntitySelection(record);
			focusTargetView();
		});
		table.onSortingChanged.addListener(event -> {
			perspectiveModel.handleSortRequest(event.getSortField(), event.getSortDirection() == SortDirection.ASC);
		});
		createTable(table);
	}

	public void addFilterField(AbstractField<?> field, String title, Icon<?, ?> icon) {
		filterFields.add(new FilterField(field, title, icon));
		field.onValueChanged.addListener(perspectiveModel::updateModel);
	}

	public abstract void createTable(ListTable<ENTITY> table);

	public void addSorter(String fieldName, Comparator<ENTITY> comparator) {
		perspectiveModel.addSorter(fieldName, comparator);
	}

	public void addStringSorter(String fieldName, Function<ENTITY, String> entityStringFunction) {
		perspectiveModel.addStringSorter(fieldName, entityStringFunction);
	}

	public void addIntegerSorter(String fieldName, Function<ENTITY, Integer> entityIntegerFunction) {
		perspectiveModel.addIntegerSorter(fieldName, entityIntegerFunction);
	}

	public void addLongSorter(String fieldName, Function<ENTITY, Long> entityLongFunction) {
		perspectiveModel.addLongSorter(fieldName, entityLongFunction);
	}

	public void addBooleanSorter(String fieldName, Function<ENTITY, Boolean> entityBooleanFunction) {
		perspectiveModel.addBooleanSorter(fieldName, entityBooleanFunction);
	}
	
	public ListTable<ENTITY> getTable() {
		return table;
	}

	@Override
	public Component getViewComponent() {
		return verticalLayout;
	}
}
