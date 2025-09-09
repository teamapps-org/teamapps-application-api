package org.teamapps.application.ui.timefilter;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.event.TwoWayBindableValueFireAlways;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.PropertyData;
import org.teamapps.application.ux.combo.ComboBoxUtils;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.index.FieldIndex;
import org.teamapps.universaldb.index.IndexType;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.numeric.IntegerIndex;
import org.teamapps.universaldb.index.numeric.LongIndex;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.CheckBox;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.timegraph.*;
import org.teamapps.ux.component.timegraph.graph.LineGraph;
import org.teamapps.ux.component.timegraph.model.LineGraphModel;
import org.teamapps.ux.component.timegraph.model.timestamps.PartitioningTimestampsLineGraphModel;
import org.teamapps.ux.component.timegraph.model.timestamps.StaticTimestampsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TimeGraphView<ENTITY extends Entity<ENTITY>> extends AbstractLazyRenderingApplicationView {

	private final TimeGraphModel<ENTITY> model;
	private final AbstractUdbEntity<ENTITY> entityBuilder;
	private TimeGraph timeGraph;
	private final TwoWayBindableValue<TimeGraphLine<ENTITY>> selectedGraphLine = TwoWayBindableValueFireAlways.create();
	private final List<TimeGraphLine<ENTITY>> timeGraphLines = new ArrayList<>();

	private ScaleType scaleType = ScaleType.SYMLOG;
	private final Event<Void> onUpdateGraphRequested = new Event<>();

	public TimeGraphView(TimeGraphModel<ENTITY> model, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.model = model;
		entityBuilder = (AbstractUdbEntity<ENTITY>) model.getEntityBuilder();
	}

	public void setDefaultLines() {
		addMetaDataFilters();
		initialize();
	}

	@Override
	public void createUi() {
		timeGraph = new TimeGraph();

		Consumer<TimeGraphLine<ENTITY>> graphUpdater = timeGraphLine -> {
			LineGraph lineGraph = createLineGraph(timeGraphLine);
			timeGraph.setGraphs(List.of(lineGraph));
		};

		selectedGraphLine.onChanged().addListener(graphUpdater::accept);

		timeGraph.onIntervalSelected.addListener(interval -> {
			if (interval == null || interval.getMax() == 0) {
				model.handleTimeFilter(null);
			} else {
				TimeGraphLine<ENTITY> graphLine = selectedGraphLine.get();
				Predicate<ENTITY> timeFilter = graphLine.createTimeFilter(interval.getMin(), interval.getMax());
				model.handleTimeFilter(entityStream -> entityStream.filter(timeFilter::test));
			}
		});

		model.getTimeGraphEntities().onChanged().addListener(records -> {
			if (isVisible()) {
				graphUpdater.accept(selectedGraphLine.get());
			}
		});

		graphUpdater.accept(selectedGraphLine.get());

		onUpdateGraphRequested.addListener(() -> graphUpdater.accept(selectedGraphLine.get()));
	}

	private LineGraph createLineGraph(TimeGraphLine<ENTITY> timeGraphLine) {
		RgbaColor color = timeGraphLine.getColor();
		LineGraphModel lineGraphModel = createLineGraphModel(timeGraphLine);
		LineGraph lineGraph = new LineGraph(lineGraphModel, LineChartCurveType.MONOTONE, 0.5f, color, color.withAlpha(0.05f));
		lineGraph.setAreaColorScaleMin(color.withAlpha(0.02f));
		lineGraph.setAreaColorScaleMax(color.withAlpha(0.5f));
		lineGraph.setYScaleType(scaleType);
		lineGraph.setYScaleZoomMode(LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO);
		lineGraph.setYZeroLineVisible(false);
		return lineGraph;
	}

	private LineGraphModel createLineGraphModel(TimeGraphLine<ENTITY> timeGraphLine) {
		StaticTimestampsModel timestampsModel = new StaticTimestampsModel() {
			@Override
			public Interval getDomainX() {
				Interval domainX = super.getDomainX();
				long diff = (domainX.getMax() - domainX.getMin()) / 10;
				return new Interval(domainX.getMin() - diff, domainX.getMax() + diff);
			}
		};
		Supplier<long[]> dataSupplier = createTimeGraphDataSupplier(timeGraphLine);
		model.getTimeGraphEntities().onChanged().addListener(() -> timestampsModel.setEventTimestamps(dataSupplier.get()));
		timestampsModel.setEventTimestamps(dataSupplier.get());
		return new PartitioningTimestampsLineGraphModel(timestampsModel);
	}

	private Supplier<long[]> createTimeGraphDataSupplier(TimeGraphLine<ENTITY> timeGraphLine) {
		Function<ENTITY, Long> recordToTimeFunction = timeGraphLine.getRecordToTimeFunction();
		Function<ENTITY, Long> timeIgnoreIfSameFunction = timeGraphLine.getRecordToTimeIgnoreIfSameFunction();
		return () -> {
			List<ENTITY> records = model.getTimeGraphEntities().get();
			long[] data = new long[records.size()];
			int pos = 0;
			for (int i = 0; i < records.size(); i++) {
				ENTITY entity = records.get(i);
				long value = recordToTimeFunction.apply(entity);
				if (value > 0) {
					long otherValue = timeIgnoreIfSameFunction != null ? timeIgnoreIfSameFunction.apply(entity) : 0;
					if (value != otherValue) {
						data[pos] = value;
						pos++;
					}
				}
			}
			long[] actualData = new long[pos];
			System.arraycopy(data, 0, actualData, 0, pos);
			return actualData;
		};
	}

	@Override
	public Component getViewComponent() {
		return timeGraph;
	}

	public void initialize() {
		Panel panel = getParentPanel();
		addTimeGraphSelectionComboBox(panel);
		selectedGraphLine.set(timeGraphLines.getFirst());

		CheckBox logarithmicGraphCheckBox = new CheckBox(getLocalized("app.logarithmicScale"));
		logarithmicGraphCheckBox.setValue(true);
		logarithmicGraphCheckBox.onValueChanged.addListener(logarithmic -> {
			scaleType = logarithmic ? ScaleType.SYMLOG : ScaleType.LINEAR;
			onUpdateGraphRequested.fire();
		});
		panel.setLeftHeaderField(logarithmicGraphCheckBox);
	}

	public void addTimeGraphSelectionComboBox(Panel panel) {
		ComboBox<TimeGraphLine<ENTITY>> comboBox = ComboBoxUtils.createRecordComboBox(
				() -> timeGraphLines.stream().toList(),
				(line, propertyNames) -> PropertyData.create(line.getIcon(), line.getTitle()),
				BaseTemplate.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE
		);
		comboBox.onValueChanged.addListener(selectedGraphLine::set);
		panel.setRightHeaderField(comboBox);
		comboBox.setValue(timeGraphLines.getFirst());
	}

	public void addTimeFilter(String fieldName, RgbaColor color, Icon<?, ?> icon, String title) {
		FieldIndex fieldIndex = entityBuilder.getTableIndex().getFieldIndex(fieldName);
		Function<ENTITY, Long> recordToTimeFunction;
		if (fieldIndex.getType() == IndexType.INT) {
			recordToTimeFunction = createIntegerRecordToTimeFunction(fieldName);
		} else {
			recordToTimeFunction = createLongRecordToTimeFunction(fieldName);
		}
		timeGraphLines.add(new TimeGraphLine<>(fieldName, icon, title, color, recordToTimeFunction));
	}

	public void addMetaDataFilters() {
		timeGraphLines.add(createDateCreatedLine());
		timeGraphLines.add(createDateModifiedLine());
		timeGraphLines.add(crateDateRestoredLine());
		timeGraphLines.add(createDateDeletedLine());
	}

	public TimeGraphLine<ENTITY> createDateCreatedLine() {
		String fieldName = Table.FIELD_CREATION_DATE;
		return new TimeGraphLine<>(fieldName, getIcon(fieldName), getTitle(fieldName), RgbaColor.MATERIAL_GREEN_700, createIntegerRecordToTimeFunction(fieldName));
	}

	public TimeGraphLine<ENTITY> createDateModifiedLine() {
		String fieldName = Table.FIELD_MODIFICATION_DATE;
		return new TimeGraphLine<>(fieldName, getIcon(fieldName), getTitle(fieldName), RgbaColor.MATERIAL_BLUE_700, createIntegerRecordToTimeFunction(fieldName), createIntegerRecordToTimeFunction(Table.FIELD_CREATION_DATE));
	}

	public TimeGraphLine<ENTITY> createDateDeletedLine() {
		String fieldName = Table.FIELD_DELETION_DATE;
		return new TimeGraphLine<>(fieldName, getIcon(fieldName), getTitle(fieldName), RgbaColor.MATERIAL_RED_600, createIntegerRecordToTimeFunction(fieldName)).setShowOnDeletedRecords(true);
	}

	public TimeGraphLine<ENTITY> crateDateRestoredLine() {
		String fieldName = Table.FIELD_RESTORE_DATE;
		return new TimeGraphLine<>(fieldName, getIcon(fieldName), getTitle(fieldName), RgbaColor.MATERIAL_ORANGE_500, createIntegerRecordToTimeFunction(fieldName));
	}

	private Function<ENTITY, Long> createIntegerRecordToTimeFunction(String fieldName) {
		TableIndex tableIndex = entityBuilder.getTableIndex();
		IntegerIndex timestampIndex = (IntegerIndex) tableIndex.getFieldIndex(fieldName);
		return record -> timestampIndex.getValue(record.getId()) * 1_000L;
	}

	private Function<ENTITY, Long> createLongRecordToTimeFunction(String fieldName) {
		TableIndex tableIndex = entityBuilder.getTableIndex();
		LongIndex dateIndex = (LongIndex) tableIndex.getFieldIndex(fieldName);
		return record -> dateIndex.getValue(record.getId());
	}

	private String getTitle(String fieldName) {
		return switch (fieldName) {
			case Table.FIELD_CREATION_DATE -> getLocalized(Dictionary.CREATION_DATE);
			case Table.FIELD_MODIFICATION_DATE -> getLocalized(Dictionary.MODIFICATION_DATE);
			case Table.FIELD_DELETION_DATE -> getLocalized(Dictionary.DELETION_DATE);
			case Table.FIELD_RESTORE_DATE -> getLocalized(Dictionary.RESTORE_DATE);
			default -> fieldName;
		};
	}

	private Icon<?, ?> getIcon(String fieldName) {
		return switch (fieldName) {
			case Table.FIELD_CREATION_DATE -> ApplicationIcons.ADD;
			case Table.FIELD_MODIFICATION_DATE -> ApplicationIcons.EDIT;
			case Table.FIELD_DELETION_DATE -> ApplicationIcons.GARBAGE_EMPTY;
			case Table.FIELD_RESTORE_DATE -> ApplicationIcons.GARBAGE_MAKE_EMPTY;
			default -> ApplicationIcons.CALENDAR;
		};
	}
}
