package org.teamapps.application.ui.table;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.AbstractPerspectiveModel;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.table.ListTable;

public class EntityTableFilterView<ENTITY extends Entity<ENTITY>> extends AbstractEntityTableView<ENTITY>{


	public EntityTableFilterView(AbstractPerspectiveModel<ENTITY> perspectiveModel, ApplicationInstanceData applicationInstanceData) {
		super(perspectiveModel, applicationInstanceData);
	}

	@Override
	public void createTable(ListTable<ENTITY> table) {

	}
}
