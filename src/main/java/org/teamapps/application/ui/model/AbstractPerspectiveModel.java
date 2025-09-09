package org.teamapps.application.ui.model;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.entity.EntityUpdateType;
import org.teamapps.application.api.event.TwoWayBindableValueFireAlways;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.SimpleOrganizationalPrivilege;
import org.teamapps.application.api.privilege.StandardPrivilegeGroup;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ui.changehistory.EntityChangeHistoryModel;
import org.teamapps.application.ui.drilldown.DrillDownView;
import org.teamapps.application.ui.notification.NotificationUpdateController;
import org.teamapps.application.ui.timefilter.TimeGraphView;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.common.format.Color;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.index.numeric.IntegerIndex;
import org.teamapps.universaldb.index.numeric.NumericFilter;
import org.teamapps.universaldb.pojo.AbstractUdbQuery;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.application.layout.ExtendedLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public abstract class AbstractPerspectiveModel<ENTITY extends Entity<ENTITY>> implements FilterableEntityModel<ENTITY>, SortableEntityModel<ENTITY>, LifecycleEntityModel<ENTITY>, UpdateListenerEntityModel<ENTITY>, EntityChangeHistoryModel<ENTITY> {

	private final Event<ENTITY> onEntityCreated = new Event<>();
	private final Event<ENTITY> onEntityUpdated = new Event<>();
	private final Event<ENTITY> onEntityDeleted = new Event<>();
	private final Event<ENTITY> onEntityRestored = new Event<>();

	private final List<BiFunction<ENTITY, ENTITY, Runnable>> beforeUpdateAndAfterUpdateHandlers = new ArrayList<>();

	private final Event<EntityUpdateType> onSelectedEntityExternallyChanged = new Event<>();
	private final Event<EntityUpdateType> onEntityExternallyChanged = new Event<>();
	private final TwoWayBindableValue<List<ENTITY>> entities = TwoWayBindableValueFireAlways.create();
	private final TwoWayBindableValue<Integer> recordCount = TwoWayBindableValue.create(0);
	private final TwoWayBindableValue<List<ENTITY>> facetEntities = TwoWayBindableValueFireAlways.create(Collections.emptyList());
	private final TwoWayBindableValue<List<ENTITY>> timeGraphEntities = TwoWayBindableValueFireAlways.create();
	private final TwoWayBindableValue<ENTITY> selectedEntity = TwoWayBindableValueFireAlways.create();
	private final TwoWayBindableValue<Boolean> showDeletedEntities = TwoWayBindableValue.create(false);
	private final EntityBuilder<ENTITY> entityBuilder;
	private final ApplicationInstanceData applicationInstanceData;
	private final List<TextField> fullTextQueryFields = new ArrayList<>();
	private final Map<String, Function<Stream<ENTITY>, Stream<ENTITY>>> customEntityFilters = new HashMap<>();
	private final DrillDownView<ENTITY> drillDownView;
	private final TimeGraphView<ENTITY> timeGraphView;

	private int deletionRetentionInDays = 90;

	private String fullTextQuery;
	private Function<Stream<ENTITY>, Stream<ENTITY>> baseFilter;
	private Function<Stream<ENTITY>, Stream<ENTITY>> facetFilterFunction;
	private Function<Stream<ENTITY>, Stream<ENTITY>> timeFilterFunction;
	private BiFunction<String, Stream<ENTITY>, Stream<ENTITY>> customFulltextFilter;
	private BiConsumer<String, Query<ENTITY>> customFullTextFilterHandler;

	private Set<OrganizationUnitView> orgUnitPrivilegeFilter;
	private String orgUnitPrivilegeFilterTableColumnName;

	private boolean reverseSort;
	private String sortFieldName;
	private boolean sortAscending;
	private String[] sortPath;
	private Map<String, Comparator<ENTITY>> sorterMap = new HashMap<>();

	private final List<Function<ENTITY, ValidationMessage>> saveValidators = new ArrayList<>();
	private final List<Function<ENTITY, ValidationMessage>> deletionValidators = new ArrayList<>();
	private final List<Function<ENTITY, ValidationMessage>> restoreValidators = new ArrayList<>();
	private BiConsumer<ENTITY, Runnable> saveDialogueHandler;
	private BiConsumer<ENTITY, Runnable> deleteDialogueHandler;
	private BiConsumer<ENTITY, Runnable> restoreDialogueHandler;

	private NotificationUpdateController<ENTITY> notificationUpdateController;

	public AbstractPerspectiveModel(EntityBuilder<ENTITY> entityBuilder, ApplicationInstanceData applicationInstanceData) {
		this.entityBuilder = entityBuilder;
		this.applicationInstanceData = applicationInstanceData;
		entities.onChanged().addListener(list -> recordCount.set(list.size()));

		drillDownView = new DrillDownView<>(this, Entity::getEntityValue, applicationInstanceData);
		timeGraphView = new TimeGraphView<>(this, applicationInstanceData);

		applicationInstanceData.registerEntityUpdateListener(entityBuilder, entityEntityUpdate -> {
			EntityUpdateType updateType = entityEntityUpdate.getUpdateType();
			ENTITY entity = entityEntityUpdate.getEntity();
			if (entity.equals(selectedEntity.get())) {
				onSelectedEntityExternallyChanged.fire(updateType);
			}
			if (matchesFilters(entity)) {
				updateModel();
				onEntityExternallyChanged.fire(updateType);
			}
		});
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	@Override
	public NotificationUpdateController<ENTITY> getNotificationUpdateController(Function<ENTITY, OrganizationUnitView> unitByEntityFunction) {
		if (notificationUpdateController == null) {
			notificationUpdateController = NotificationUpdateController.createNotificationUpdateController(this, unitByEntityFunction, applicationInstanceData);
		}
		return notificationUpdateController;
	}

	public TimeGraphView<ENTITY> getTimeGraphView() {
		return timeGraphView;
	}

	public DrillDownView<ENTITY> getDrillDownView() {
		return drillDownView;
	}

	public void setOrgPrivilege(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, String orgFieldName) {
		this.orgUnitPrivilegeFilter = new HashSet<>(applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup));
		this.orgUnitPrivilegeFilterTableColumnName = orgFieldName;
	}

	public void setOrgPrivilege(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, String orgFieldName) {
		this.orgUnitPrivilegeFilter = new HashSet<>(applicationInstanceData.getAllowedUnits(simpleOrganizationalPrivilege));
		this.orgUnitPrivilegeFilterTableColumnName = orgFieldName;
	}

	public void setDeletionRetentionInDays(int deletionRetentionInDays) {
		this.deletionRetentionInDays = deletionRetentionInDays;
	}

	public void setPredicateBaseFilter(Predicate<ENTITY> predicate) {
		setBaseFilter(entityStream -> entityStream.filter(predicate));
	}

	public void setBooleanBaseFilter(Function<ENTITY, Boolean> baseFilter) {
		setBaseFilter(entityStream -> entityStream.filter(baseFilter::apply));
	}

	public void setBaseFilter(Function<Stream<ENTITY>, Stream<ENTITY>> baseFilter) {
		this.baseFilter = baseFilter;
		updateModel();
	}

	@Override
	public void setCustomFulltextFilter(BiFunction<String, Stream<ENTITY>, Stream<ENTITY>> customFulltextFilter) {
		this.customFulltextFilter = customFulltextFilter;
	}

	@Override
	public void setCustomFullTextFilter(BiConsumer<String, Query<ENTITY>> fullTextFilterHandler) {
		this.customFullTextFilterHandler = fullTextFilterHandler;
	}

	@Override
	public void addCustomEntityFilter(String filterName, Function<Stream<ENTITY>, Stream<ENTITY>> customEntityFilter) {
		customEntityFilters.put(filterName, customEntityFilter);
		updateModel();
	}

	@Override
	public void removeCustomEntityFilter(String filterName) {
		customEntityFilters.remove(filterName);
		updateModel();
	}

	@Override
	public void addSorter(String fieldName, Comparator<ENTITY> comparator) {
		sorterMap.put(fieldName, comparator);
	}

	@Override
	public Event<EntityUpdateType> getOnSelectedEntityExternallyChanged() {
		return onSelectedEntityExternallyChanged;
	}

	@Override
	public Event<EntityUpdateType> getOnEntityExternallyChanged() {
		return onEntityExternallyChanged;
	}

	@Override
	public EntityBuilder<ENTITY> getEntityBuilder() {
		return entityBuilder;
	}

	@Override
	public ObservableValue<ENTITY> getSelectedEntity() {
		return selectedEntity;
	}

	@Override
	public ObservableValue<List<ENTITY>> getEntities() {
		return entities;
	}

	@Override
	public ObservableValue<Integer> getEntityCount() {
		return recordCount;
	}

	@Override
	public ObservableValue<Boolean> getShowDeletedEntities() {
		return showDeletedEntities;
	}

	@Override
	public ObservableValue<List<ENTITY>> getFacetEntities() {
		return facetEntities;
	}

	@Override
	public ObservableValue<List<ENTITY>> getTimeGraphEntities() {
		return timeGraphEntities;
	}

	@Override
	public void handleEntitySelection(ENTITY record) {
		selectedEntity.set(record);
	}

	@Override
	public void handleShowDeletedEntities(boolean showDeletedEntities) {
		this.showDeletedEntities.set(showDeletedEntities);
		updateModel();
	}

	@Override
	public void handleFacetSelection(List<ENTITY> entities) {
		this.entities.set(entities);
		timeGraphEntities.set(entities);
	}

	@Override
	public void handleFacetFilter(Function<Stream<ENTITY>, Stream<ENTITY>> facetFilterFunction) {
		this.facetFilterFunction = facetFilterFunction;
		updateModel();
	}

	@Override
	public void handleTimeFilter(Function<Stream<ENTITY>, Stream<ENTITY>> timeFilterFunction) {
		this.timeFilterFunction = timeFilterFunction;
		updateModel();
	}

	@Override
	public void handleFullTextQuery(String query) {
		if (query != null && !query.isEmpty()) {
			this.fullTextQuery = query.toLowerCase();
		} else {
			this.fullTextQuery = null;
		}
		updateModel();
	}

	@Override
	public void handleReverseSort(boolean reverseSort) {
		this.reverseSort = reverseSort;
		sortFieldName = null;
		updateModel();
	}

	@Override
	public void handleSortLastModifiedFirst(boolean sortLastModified) {
		sortFieldName = Table.FIELD_MODIFICATION_DATE;
		sortAscending = false;
		reverseSort = false;
		updateModel();
	}

	@Override
	public void handleSortRequest(String fieldName, boolean ascending, String... sortPath) {
		this.sortFieldName = fieldName;
		this.sortAscending = ascending;
		this.sortPath = sortPath;
		reverseSort = false;
		updateModel();
	}

	public void selectFirstRecord() {
		List<ENTITY> entities = this.entities.get();
		if (entities != null && !entities.isEmpty()) {
			selectedEntity.set(entities.getFirst());
		}
	}

	@Override
	public void handleSelectPreviousEntity() {
		List<ENTITY> list = entities.get();
		if (list != null) {
			ENTITY selectedEntity = this.selectedEntity.get();
			ENTITY previousEntity = null;
			boolean entityFound = false;
			for (ENTITY entity : list) {
				if (entityFound && previousEntity != null) {
					handleEntitySelection(previousEntity);
					entityFound = false;
					break;
				}
				if (entity.equals(selectedEntity)) {
					entityFound = true;
				} else {
					entityFound = false;
					previousEntity = entity;
				}
			}
			if (entityFound && previousEntity != null) {
				handleEntitySelection(previousEntity);
			}
		}
	}

	@Override
	public void handleSelectNextEntity() {
		List<ENTITY> list = entities.get();
		if (list != null) {
			ENTITY selectedEntity = this.selectedEntity.get();
			boolean selectNext = false;
			for (ENTITY entity : list) {
				if (selectNext) {
					handleEntitySelection(entity);
					break;
				}
				if (entity.equals(selectedEntity)) {
					selectNext = true;
				}
			}
		}
	}

	@Override
	public void handleSelectFirstEntity() {
		selectFirstRecord();
	}

	@Override
	public void setDeletionDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		deleteDialogueHandler = dialogueHandler;
	}

	@Override
	public void setRestoreDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		restoreDialogueHandler = dialogueHandler;
	}

	@Override
	public void setSaveDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		saveDialogueHandler = dialogueHandler;
	}

	@Override
	public void addDeletionValidator(Function<ENTITY, ValidationMessage> validator) {
		deletionValidators.add(validator);
	}

	@Override
	public void addRestoreValidator(Function<ENTITY, ValidationMessage> validator) {
		restoreValidators.add(validator);
	}

	@Override
	public void addSaveValidator(Function<ENTITY, ValidationMessage> validator) {
		saveValidators.add(validator);
	}

	@Override
	public void addCreationListener(Consumer<ENTITY> listener) {
		onEntityCreated.addListener(listener);
	}

	@Override
	public void addUpdateListener(Consumer<ENTITY> listener) {
		onEntityUpdated.addListener(listener);
	}

	@Override
	public void addDeletionListener(Consumer<ENTITY> listener) {
		onEntityDeleted.addListener(listener);
	}

	@Override
	public void addRestoreListener(Consumer<ENTITY> listener) {
		onEntityRestored.addListener(listener);
	}

	@Override
	public void addBeforeUpdateAndAfterUpdateHandler(BiFunction<ENTITY, ENTITY, Runnable> beforeUpdateAndAfterHandler) {
		beforeUpdateAndAfterUpdateHandlers.add(beforeUpdateAndAfterHandler);
	}

	private boolean validateEntityAction(ENTITY entity, List<Function<ENTITY, ValidationMessage>> validators) {
		boolean validated = true;
		for (Function<ENTITY, ValidationMessage> validatorFunction : validators) {
			ValidationMessage validationMessage = validatorFunction.apply(entity);
			if (validationMessage != null) {
				validated = false;
				UiUtils.showNotification(validationMessage.getIcon(), validationMessage.getMessage());
			}
		}
		return validated;
	}

	@Override
	public void saveEntity(ENTITY entity) {
		saveEntity(entity, null);
	}

	@Override
	public void saveEntity(ENTITY entity, Runnable onSuccessRunnable) {
		Runnable saveRunnable = () -> {
			List<ValidationMessage> validationMessages = saveValidators.stream()
					.map(validatorFunction -> validatorFunction.apply(entity))
					.filter(Objects::nonNull)
					.toList();
			if (!validationMessages.isEmpty()) {
				for (ValidationMessage validationMessage : validationMessages) {
					UiUtils.showNotification(validationMessage.getIcon(), validationMessage.getMessage());
				}
			} else {
				boolean stored = entity.isStored();
				List<Runnable> afterSaveRunnables = new ArrayList<>();
				if (stored && !beforeUpdateAndAfterUpdateHandlers.isEmpty()) {
					beforeUpdateAndAfterUpdateHandlers.forEach(handler -> {
						Runnable afterUpdateRunnable = handler.apply(entity, entityBuilder.build(entity.getId()));
						if (afterUpdateRunnable != null) {
							afterSaveRunnables.add(afterUpdateRunnable);
						}
					});
				}
				entity.save();
				updateModel();
				afterSaveRunnables.forEach(Runnable::run);
				if (stored) {
					onEntityUpdated.fire(entity);
				} else {
					onEntityCreated.fire(entity);
				}
				if (onSuccessRunnable != null) {
					onSuccessRunnable.run();
				}
				handleEntitySelection(entity);
			}
		};

		if (validateEntityAction(entity, saveValidators)) {
			if (saveDialogueHandler != null) {
				saveDialogueHandler.accept(entity, saveRunnable);
			} else {
				saveRunnable.run();
			}
		}
	}

	@Override
	public void deleteEntity(ENTITY entity) {
		deleteEntity(entity, null);
	}

	@Override
	public void deleteEntity(ENTITY entity, Runnable onSuccessRunnable) {
		Runnable deleteRunnable = () -> {
			List<ValidationMessage> validationMessages = deletionValidators.stream()
					.map(validatorFunction -> validatorFunction.apply(entity))
					.filter(Objects::nonNull)
					.toList();
			if (!validationMessages.isEmpty()) {
				for (ValidationMessage validationMessage : validationMessages) {
					UiUtils.showNotification(validationMessage.getIcon(), validationMessage.getMessage());
				}
			} else {
				entity.delete();
				handleSelectNextEntity();
				updateModel();
				onEntityDeleted.fire(entity);
				if (onSuccessRunnable != null) {
					onSuccessRunnable.run();
				}
			}
		};
		if (validateEntityAction(entity, deletionValidators)) {
			if (deleteDialogueHandler != null) {
				deleteDialogueHandler.accept(entity, deleteRunnable);
			} else {
				deleteRunnable.run();
			}
		}
	}

	@Override
	public void restoreEntity(ENTITY entity) {
		restoreEntity(entity, null);
	}

	@Override
	public void restoreEntity(ENTITY entity, Runnable onSuccessRunnable) {
		Runnable restoreRunnable = () -> {
			List<ValidationMessage> validationMessages = restoreValidators.stream()
					.map(validatorFunction -> validatorFunction.apply(entity))
					.filter(Objects::nonNull)
					.toList();
			if (!validationMessages.isEmpty()) {
				for (ValidationMessage validationMessage : validationMessages) {
					UiUtils.showNotification(validationMessage.getIcon(), validationMessage.getMessage());
				}
			} else {
				entity.restoreDeleted();
				handleSelectNextEntity();
				updateModel();
				onEntityRestored.fire(entity);
				if (onSuccessRunnable != null) {
					onSuccessRunnable.run();
				}
			}
		};
		if (validateEntityAction(entity, restoreValidators)) {
			if (restoreDialogueHandler != null) {
				restoreDialogueHandler.accept(entity, restoreRunnable);
			} else {
				restoreRunnable.run();
			}
		}
	}

	public abstract Query<ENTITY> getBaseQuery();

	public Stream<ENTITY> getUnfilteredEntitiesStream() {
		boolean deletedEntities = showDeletedEntities.get();
		Query<ENTITY> query = getBaseQuery();
		AbstractUdbQuery<ENTITY> udbQuery = (AbstractUdbQuery<ENTITY>) query;

		if (orgUnitPrivilegeFilterTableColumnName != null) {
			IntegerIndex integerIndex = (IntegerIndex) udbQuery.getTableIndex().getFieldIndex(orgUnitPrivilegeFilterTableColumnName);
			udbQuery.and(integerIndex.createFilter(NumericFilter.containsEntitiesFilter(orgUnitPrivilegeFilter)));
		}

		if (fullTextQuery != null) {
			if (customFullTextFilterHandler != null) {
				customFullTextFilterHandler.accept(fullTextQuery, query);
			} else {
				udbQuery.and(udbQuery.getTableIndex().createFullTextFilter(fullTextQuery, applicationInstanceData.getUser()));
			}
		}

		if (deletedEntities) {
			IntegerIndex integerIndex = (IntegerIndex) udbQuery.getTableIndex().getFieldIndex(Table.FIELD_DELETION_DATE);
			udbQuery.and(integerIndex.createFilter(NumericFilter.greaterFilter(Instant.now().minus(deletionRetentionInDays, ChronoUnit.DAYS).getEpochSecond())));
		}

		if (sortFieldName != null && !sorterMap.containsKey(sortFieldName)) {
			return udbQuery.execute(deletedEntities, sortFieldName, sortAscending, applicationInstanceData.getUser(), sortPath).stream();
		}

		return udbQuery.execute(deletedEntities).stream();
	}

	public void updateModel() {
		Stream<ENTITY> recordsStream = getUnfilteredEntitiesStream();
		if (baseFilter != null) {
			recordsStream = baseFilter.apply(recordsStream);
		}
		if (facetFilterFunction != null) {
			recordsStream = facetFilterFunction.apply(recordsStream);
		}

		for (Function<Stream<ENTITY>, Stream<ENTITY>> filterFunction : customEntityFilters.values()) {
			recordsStream = filterFunction.apply(recordsStream);
		}

		if (customFulltextFilter != null && fullTextQuery != null) {
			recordsStream = customFulltextFilter.apply(fullTextQuery, recordsStream);
		}

		if (sortFieldName != null && sorterMap.containsKey(sortFieldName)) {
			Comparator<ENTITY> entityComparator = sorterMap.get(sortFieldName);
			if (!sortAscending) {
				entityComparator = entityComparator.reversed();
			}
			recordsStream = recordsStream.sorted(entityComparator);
		}

		List<ENTITY> recordList = recordsStream.toList();

		if (sortFieldName == null && reverseSort) {
			recordList = new ArrayList<>(recordList);
			Collections.reverse(recordList);
		}

		timeGraphEntities.set(recordList);
		if (timeFilterFunction != null) {
			recordsStream = recordList.stream();
			recordsStream = timeFilterFunction.apply(recordsStream);
			recordList = recordsStream.toList();
		}
		entities.set(recordList);
		facetEntities.set(recordList);
	}

	private boolean matchesFilters(ENTITY entity) {
		if (getBaseQuery().matches(entity)) {
			if (facetFilterFunction != null || timeFilterFunction != null) {
				Stream<ENTITY> stream = List.of(entity).stream();
				if (facetFilterFunction != null) {
					stream = facetFilterFunction.apply(stream);
				}
				if (timeFilterFunction != null) {
					stream = timeFilterFunction.apply(stream);
				}
				return stream.allMatch(entity::equals);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public SimpleItemView<AbstractLazyRenderingApplicationView> createToolsViewsAndButtons(Perspective perspective, OrganizationalPrivilegeGroup organizationalPrivilegeGroup) {
		return createToolsViewsAndButtons(perspective, !applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.SHOW_RECYCLE_BIN).isEmpty());
	}

	public SimpleItemView<AbstractLazyRenderingApplicationView> createToolsViewsAndButtons(Perspective perspective, StandardPrivilegeGroup privilegeGroup) {
		return createToolsViewsAndButtons(perspective, applicationInstanceData.isAllowed(privilegeGroup, Privilege.SHOW_RECYCLE_BIN));
	}

	//todo deletion privileges (units) may differ from read/update units! needs separate allowed units for recycle-bin vs read
	public SimpleItemView<AbstractLazyRenderingApplicationView> createToolsViewsAndButtons(Perspective perspective, boolean showDeletedEntities) {
		View topView = perspective.addView(View.createView(ExtendedLayout.TOP, ApplicationIcons.CHART_LINE, applicationInstanceData.getLocalized("apps.timeFilter"), null));
		View innerLeftBottomView = perspective.addView(View.createView(ExtendedLayout.INNER_LEFT_BOTTOM, ApplicationIcons.CHART_COLUMN, applicationInstanceData.getLocalized("apps.groupFilter"), null));
		topView.getPanel().setBodyBackgroundColor(Color.WHITE.withAlpha(0.9f));
		topView.setVisible(false);
		innerLeftBottomView.setVisible(false);

		timeGraphView.setParentView(topView);
		drillDownView.setParentView(innerLeftBottomView);
		timeGraphView.setDefaultLines();

		ToolbarButtonGroup buttonGroup = perspective.addWorkspaceButtonGroup(new ToolbarButtonGroup());
		ToolbarButton hideDeletedButton = buttonGroup.addButton(ToolbarButton.create(CompositeIcon.of(ApplicationIcons.GARBAGE_EMPTY, ApplicationIcons.ERROR), applicationInstanceData.getLocalized("apps.hideRecycleBin"), applicationInstanceData.getLocalized("apps.hideRecycleBin")));
		hideDeletedButton.setVisible(false);
		hideDeletedButton.onClick.addListener(() -> {
			hideDeletedButton.setVisible(false);
			handleShowDeletedEntities(false);
		});

		buttonGroup = perspective.addWorkspaceButtonGroup(new ToolbarButtonGroup());
		ToolbarButton hideTimeGraphButton = buttonGroup.addButton(ToolbarButton.create(CompositeIcon.of(ApplicationIcons.CLOCK_FORWARD, ApplicationIcons.ERROR), applicationInstanceData.getLocalized("apps.hideTimeFilter"), applicationInstanceData.getLocalized("apps.hideTimeFilter")));
		hideTimeGraphButton.setVisible(false);
		hideTimeGraphButton.onClick.addListener(() -> {
			hideTimeGraphButton.setVisible(false);
			timeGraphView.hide();
		});

		buttonGroup = perspective.addWorkspaceButtonGroup(new ToolbarButtonGroup());
		ToolbarButton hideDrillDownViewButton = buttonGroup.addButton(ToolbarButton.create(CompositeIcon.of(ApplicationIcons.CHART_COLUMN, ApplicationIcons.ERROR), applicationInstanceData.getLocalized("apps.hideGroupFilter"), applicationInstanceData.getLocalized("apps.hideGroupFilter")));
		hideDrillDownViewButton.setVisible(false);
		hideDrillDownViewButton.onClick.addListener(() -> {
			hideDrillDownViewButton.setVisible(false);
			drillDownView.hide();
		});

		buttonGroup = perspective.addWorkspaceButtonGroup(new ToolbarButtonGroup());
		ToolbarButton viewButton = buttonGroup.addButton(ToolbarButton.create(ApplicationIcons.GEARWHEEL, applicationInstanceData.getLocalized("apps.tools"), applicationInstanceData.getLocalized("apps.moreOptions")));
		viewButton.setDroDownPanelWidth(270);
		SimpleItemView<AbstractLazyRenderingApplicationView> itemView = new SimpleItemView<>();
		viewButton.setDropDownComponent(itemView);
		SimpleItemGroup<AbstractLazyRenderingApplicationView> itemGroup = itemView.addSingleColumnGroup(ApplicationIcons.WINDOWS, applicationInstanceData.getLocalized("apps.tools"));
		itemGroup.addItem(ApplicationIcons.CLOCK_FORWARD, applicationInstanceData.getLocalized("apps.timeFilter"), applicationInstanceData.getLocalized("apps.showTimeFilter")).onClick.addListener(() -> {
			timeGraphView.show(true);
			hideTimeGraphButton.setVisible(true);
		});
		itemGroup.addItem(ApplicationIcons.CHART_BAR, applicationInstanceData.getLocalized("apps.groupFilter"), applicationInstanceData.getLocalized("apps.showGroupFilter")).onClick.addListener(() -> {
			drillDownView.show(true);
			hideDrillDownViewButton.setVisible(true);
		});

		//todo check exact privileges for show recycle-bin on which org units!
		if (showDeletedEntities) {
			itemGroup = itemView.addSingleColumnGroup(ApplicationIcons.GARBAGE_EMPTY, applicationInstanceData.getLocalized("apps.showDeleted"));
			itemGroup.addItem(ApplicationIcons.GARBAGE_EMPTY, applicationInstanceData.getLocalized("apps.showDeleted"), applicationInstanceData.getLocalized("apps.showDeleted")).onClick.addListener(() -> {
				hideDeletedButton.setVisible(true);
				handleShowDeletedEntities(true);
			});
		}

		return itemView;
	}

	public void addDefaultTableViewOptions(View tableView) {
		setFullTextFilterField(tableView.getPanel());
		setPanelHeaderCountInfo(tableView.getPanel(), tableView.getPanel().getTitle());
	}

	public void setFullTextFilterField(Panel panel) {
		TextField textField = new TextField();
		fullTextQueryFields.add(textField);
		textField.setEmptyText(applicationInstanceData.getLocalized(Dictionary.SEARCH___));
		textField.setShowClearButton(true);
		textField.onTextInput.addListener(query -> {
			fullTextQueryFields.forEach(f -> {
				if (!textField.equals(f)) {
					f.setValue(query);
				}
			});
			handleFullTextQuery(query);
		});
		panel.setRightHeaderField(textField, ApplicationIcons.MAGNIFYING_GLASS, 50, 100);
	}

	public void setPanelHeaderCountInfo(Panel panel, String title) {
		panel.setTitle(title);
		recordCount.onChanged().addListener(count -> panel.setTitle((getShowDeletedEntities().get() ? "🗑" + applicationInstanceData.getLocalized("apps.deleted") + ": " : "") + title + " (" + count + ")"));
	}


}
