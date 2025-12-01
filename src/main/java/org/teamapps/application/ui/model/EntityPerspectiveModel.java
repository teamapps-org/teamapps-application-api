package org.teamapps.application.ui.model;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

public class EntityPerspectiveModel<ENTITY extends Entity<ENTITY>> extends AbstractPerspectiveModel<ENTITY>{

	private Supplier<Query<ENTITY>> querySupplier;
	private Supplier<ENTITY> newEntitySupplier;
	private Function<ENTITY, String> entityTitleFunction;

	public EntityPerspectiveModel(EntityBuilder<ENTITY> entityBuilder, ApplicationInstanceData applicationInstanceData) {
		super(entityBuilder, applicationInstanceData);
	}

	public void setModelSupplier(Supplier<Query<ENTITY>> querySupplier, Supplier<ENTITY> newEntitySupplier, Function<ENTITY, String> entityTitleFunction) {
		this.querySupplier = querySupplier;
		this.newEntitySupplier = newEntitySupplier;
		this.entityTitleFunction = entityTitleFunction;
	}

	public void setQuerySupplier(Supplier<Query<ENTITY>> querySupplier) {
		this.querySupplier = querySupplier;
	}

	public void setNewEntitySupplier(Supplier<ENTITY> newEntitySupplier) {
		this.newEntitySupplier = newEntitySupplier;
	}

	public void setEntityTitleFunction(Function<ENTITY, String> entityTitleFunction) {
		this.entityTitleFunction = entityTitleFunction;
	}

	@Override
	public Query<ENTITY> getBaseQuery() {
		return querySupplier.get();
	}

	@Override
	public Supplier<ENTITY> getNewEntitySupplier() {
		return newEntitySupplier;
	}

	@Override
	public Function<ENTITY, ENTITY> getEntityCopyFunction() {
		return entity -> getEntityBuilder().build(entity.getId());
	}

	@Override
	public String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		return entityTitleFunction.apply(entity);
	}
}
