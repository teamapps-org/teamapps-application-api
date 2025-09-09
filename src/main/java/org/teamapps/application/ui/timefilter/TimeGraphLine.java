package org.teamapps.application.ui.timefilter;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;
import java.util.function.Predicate;

public class TimeGraphLine<ENTITY extends Entity<ENTITY>> {
	private final String name;
	private final Icon icon;
	private final String title;
	private final RgbaColor color;
	private final Function<ENTITY, Long> recordToTimeFunction;
	private Function<ENTITY, Long> recordToTimeIgnoreIfSameFunction;
	private boolean showOnDeletedRecords = false;

	public TimeGraphLine(String name, Icon icon, String title, RgbaColor color, Function<ENTITY, Long> recordToTimeFunction) {
		this.name = name;
		this.icon = icon;
		this.title = title;
		this.color = color;
		this.recordToTimeFunction = recordToTimeFunction;
	}

	public TimeGraphLine(String name, Icon icon, String title, RgbaColor color, Function<ENTITY, Long> recordToTimeFunction, Function<ENTITY, Long> recordToTimeIgnoreIfSameFunction) {
		this.name = name;
		this.icon = icon;
		this.title = title;
		this.color = color;
		this.recordToTimeFunction = recordToTimeFunction;
		this.recordToTimeIgnoreIfSameFunction = recordToTimeIgnoreIfSameFunction;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public RgbaColor getColor() {
		return color;
	}

	public Function<ENTITY, Long> getRecordToTimeFunction() {
		return recordToTimeFunction;
	}

	public Function<ENTITY, Long> getRecordToTimeIgnoreIfSameFunction() {
		return recordToTimeIgnoreIfSameFunction;
	}

	public Predicate<ENTITY> createTimeFilter(long start, long end) {
		return record -> {
			Long value = recordToTimeFunction.apply(record);
			return value >= start && value <= end;
		};
	}

	public boolean isShowOnDeletedRecords() {
		return showOnDeletedRecords;
	}

	public TimeGraphLine<ENTITY> setShowOnDeletedRecords(boolean showOnDeletedRecords) {
		this.showOnDeletedRecords = showOnDeletedRecords;
		return this;
	}
}
