package org.teamapps.application.ui.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class EntityFormView<ENTITY extends Entity<ENTITY>> extends AbstractEntityFormView<ENTITY> {

	private final Function<ENTITY, Boolean> formToEntityFunction;
	private Function<ENTITY, Boolean> externalFormToEntityFunction;

	public EntityFormView(FormButtonSize buttonSize, LifecycleEntityModel<ENTITY> lifecycleEntityModel, EntityPrivileges<ENTITY> entityPrivileges, ApplicationInstanceData applicationInstanceData) {
		super(buttonSize, lifecycleEntityModel, entityPrivileges, applicationInstanceData);
		formToEntityFunction = entity -> {
			if (externalFormToEntityFunction != null) {
				return externalFormToEntityFunction.apply(entity);
			}
			return false;
		};
	}

	public void setFormToEntityFunction(Function<ENTITY, Boolean> formToEntityFunction) {
		this.externalFormToEntityFunction = formToEntityFunction;
	}

	@Override
	public Function<ENTITY, Boolean> createForm() {
		return formToEntityFunction;
	}
}
