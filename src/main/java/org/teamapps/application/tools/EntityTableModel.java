package org.teamapps.application.tools;

import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.pojo.AbstractUdbQuery;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.List;
import java.util.function.Supplier;

public class EntityTableModel<ENTITY extends Entity<ENTITY>> extends AbstractRecordTableModel<ENTITY> {

	private final Supplier<Query<ENTITY>> querySupplier;
	private final TableIndex tableIndex;
	private final EntityBuilder<ENTITY> entityBuilder;

	public EntityTableModel(Supplier<Query<ENTITY>> querySupplier) {
		this.querySupplier = querySupplier;
		AbstractUdbQuery<ENTITY> udbQuery = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		tableIndex = udbQuery.getTableIndex();
		entityBuilder = udbQuery.getEntityBuilder();
	}

	@Override
	List<ENTITY> executeQuery(String fullTextSearchString, Sorting sorting) {
		AbstractUdbQuery<ENTITY> query = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		if (fullTextSearchString != null) {
			query.addFullTextQuery(fullTextSearchString);
		}
		String sortField = sorting != null ? sorting.getFieldName() : null;
		boolean sortAscending = sorting == null || sorting.getSortDirection() == SortDirection.ASC;
		return query.execute(sortField, sortAscending, null);
	}
}
