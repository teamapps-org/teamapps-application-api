package org.teamapps.application.ui.notification;

import org.teamapps.universaldb.pojo.Entity;

import java.util.function.BiFunction;
import java.util.function.Function;

public class NotificationStatusChangedData<ENTITY extends Entity<ENTITY>> {


	private final BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction;
	private final Function<ENTITY, Integer> detailIdFunction;

	public NotificationStatusChangedData(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction) {
		this.compareUpdateWithOriginalFunction = compareUpdateWithOriginalFunction;
		this.detailIdFunction = null;
	}

	public NotificationStatusChangedData(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction, Function<ENTITY, Integer> detailIdFunction) {
		this.compareUpdateWithOriginalFunction = compareUpdateWithOriginalFunction;
		this.detailIdFunction = detailIdFunction;
	}

	public int getDetailId(ENTITY entity) {
		return detailIdFunction == null ? 0 : detailIdFunction.apply(entity);
	}

	public BiFunction<ENTITY, ENTITY, Boolean> getCompareUpdateWithOriginalFunction() {
		return compareUpdateWithOriginalFunction;
	}

	public Function<ENTITY, Integer> getDetailIdFunction() {
		return detailIdFunction;
	}
}
