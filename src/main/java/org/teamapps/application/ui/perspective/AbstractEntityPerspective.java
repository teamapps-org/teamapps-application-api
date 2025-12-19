package org.teamapps.application.ui.perspective;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.perspective.AbstractApplicationPerspective;
import org.teamapps.application.api.application.perspective.ApplicationPerspective;
import org.teamapps.application.api.application.perspective.PerspectiveBuilder;
import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.ui.drilldown.DrillDownView;
import org.teamapps.application.ui.form.EntityFormView;
import org.teamapps.application.ui.form.FormButtonSize;
import org.teamapps.application.ui.model.EntityPerspectiveModel;
import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.application.ui.table.EntityTableFilterView;
import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.ux.application.layout.ExtendedLayout;
import org.teamapps.ux.application.view.View;

import java.util.function.Supplier;

public abstract class AbstractEntityPerspective<ENTITY extends Entity<ENTITY>> extends AbstractApplicationPerspective implements PerspectiveBuilder{

	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;

	private PerspectiveBuilder perspectiveBuilder;
	private EntityTableFilterView<ENTITY> tableView;
	private EntityFormView<ENTITY> formView;
	private EntityPerspectiveModel<ENTITY> model;
	private DrillDownView<ENTITY> drillDown;

	public AbstractEntityPerspective(String name, Icon icon, String titleKey, String descriptionKey) {
		super(null, null);
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
	}

	public AbstractEntityPerspective(PerspectiveBuilder perspectiveBuilder, ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue) {
		super(applicationInstanceData, perspectiveInfoBadgeValue);
		this.perspectiveBuilder = perspectiveBuilder;
		this.name = perspectiveBuilder.getName();
		this.icon = perspectiveBuilder.getIcon();
		this.titleKey = perspectiveBuilder.getTitleKey();
		this.descriptionKey = perspectiveBuilder.getDescriptionKey();
		init();
	}

	private void init() {
		model = new EntityPerspectiveModel<>(getEntityBuilder(), getApplicationInstanceData());
		model.setModelSupplier(this::getBaseQuery, getNewEntitySupplier(), entity -> getEntityTitle(entity, getApplicationInstanceData()));
		EntityPrivileges<ENTITY> entityPrivileges = getEntityPrivileges();

		tableView = new EntityTableFilterView<>(model, getApplicationInstanceData());
		formView = new EntityFormView<>(FormButtonSize.LARGE, model, entityPrivileges, getApplicationInstanceData());

		View centerView = getPerspective().addView(View.createView(ExtendedLayout.CENTER, icon, getLocalized(titleKey), null));
		View rightView = getPerspective().addView(View.createView(ExtendedLayout.RIGHT, icon, getLocalized(titleKey), null));

		model.createToolsViewsAndButtons(getPerspective(), true);
		drillDown = model.getDrillDownView();

		formView.setParentView(rightView);
		tableView.setParentView(centerView);
		tableView.setPeerViewsToShowWhenVisible(formView);
		tableView.setTargetView(formView);

		tableView.show();
		formView.addStandardFormButtons();
		createUi();
		model.updateModel();
	}

	public abstract void createUi();

	public abstract AbstractEntityPerspective<ENTITY> createCopy(PerspectiveBuilder perspectiveBuilder, ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue);

	public abstract EntityPrivileges<ENTITY> getEntityPrivileges();

	public abstract EntityBuilder<ENTITY> getEntityBuilder();

	public abstract Query<ENTITY> getBaseQuery();

	public abstract Supplier<ENTITY> getNewEntitySupplier();

	public abstract String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData);

	public abstract boolean isAccessible(ApplicationPrivilegeProvider privilegeProvider);

	@Override
	public boolean isPerspectiveAccessible(ApplicationPrivilegeProvider privilegeProvider) {
		return isAccessible(privilegeProvider);
	}

	@Override
	public ApplicationPerspective build(ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue) {
		return createCopy(this, applicationInstanceData, perspectiveInfoBadgeValue);
	}

	public EntityPerspectiveModel<ENTITY> getModel() {
		return model;
	}

	public EntityTableFilterView<ENTITY> getTableView() {
		return tableView;
	}

	public EntityFormView<ENTITY> getFormView() {
		return formView;
	}

	public DrillDownView<ENTITY> getDrillDown() {
		return drillDown;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getTitleKey() {
		return titleKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

}
