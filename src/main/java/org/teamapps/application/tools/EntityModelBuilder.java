package org.teamapps.application.tools;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.udb.filter.TimeIntervalFilter;
import org.teamapps.universaldb.index.IndexType;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.numeric.NumericFilter;
import org.teamapps.universaldb.pojo.AbstractUdbQuery;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.List;
import java.util.function.Supplier;

public class EntityModelBuilder<ENTITY extends Entity<ENTITY>> extends RecordModelBuilder<ENTITY> {

	private final Supplier<Query<ENTITY>> querySupplier;
	private final TableIndex tableIndex;
	private final EntityBuilder<ENTITY> entityBuilder;

	public EntityModelBuilder(Supplier<Query<ENTITY>> querySupplier, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.querySupplier = querySupplier;
		AbstractUdbQuery<ENTITY> udbQuery = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		tableIndex = udbQuery.getTableIndex();
		entityBuilder = udbQuery.getEntityBuilder();
	}

	@Override
	public List<ENTITY> queryRecords(String fullTextQuery, TimeIntervalFilter timeIntervalFilter, String sortField, boolean sortAscending) {
		AbstractUdbQuery<ENTITY> query = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		if (timeIntervalFilter != null) {
			NumericFilter numericFilter = tableIndex.getColumnIndex(timeIntervalFilter.getFieldName()).getType() == IndexType.INT ? timeIntervalFilter.getIntFilter() : timeIntervalFilter.getFilter();
			query.addNumericFilter(timeIntervalFilter.getFieldName(), numericFilter);
		}
		if (fullTextQuery != null && !fullTextQuery.isBlank()) {
			query.addFullTextQuery(fullTextQuery);
		}
		return sortField != null ? query.execute(sortField, sortAscending) : query.execute();
	}
}
