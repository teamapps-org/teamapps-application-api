package org.teamapps.application.ui.drilldown;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.PropertyData;
import org.teamapps.common.format.Color;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.DisplayField;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.htmltemplate.MustacheTemplate;
import org.teamapps.ux.component.toolbutton.ToolButton;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.component.tree.TreeNodeInfoImpl;
import org.teamapps.ux.model.ListTreeModel;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrillDownView<ENTITY> extends AbstractLazyRenderingApplicationView {

	public final Event<Void> onFilterChanged = new Event<>();
	public final Event<List<ENTITY>> onDataSelected = new Event<>();

	private final DrillDownModel<ENTITY> drillDownModel;
	private VerticalLayout verticalLayout;

	private final BiFunction<ENTITY, String, Object> entityPropertyFunction;
	private final List<FacetNode<ENTITY>> filters = new ArrayList<>();
	private final List<DrillDownFacet<?, ENTITY>> facets = new ArrayList<>();
	private final Set<String> selectedFacets = new HashSet<>();

	private long lastSelection;
	private FacetNode<ENTITY> lastSelectedNode;
	private String facetQuery;


	public record FacetPair<MODEL_RECORD>(MODEL_RECORD modelRecord, Object facetRecord) {
	}

	public DrillDownView(DrillDownModel<ENTITY> drillDownModel, BiFunction<ENTITY, String, Object> entityPropertyFunction, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.drillDownModel = drillDownModel;
		this.entityPropertyFunction = entityPropertyFunction;
	}

	@Override
	public void createUi() {
		TextField queryField = new TextField();
		queryField.setEmptyText(getLocalized(Dictionary.SEARCH___));
		queryField.setShowClearButton(true);

		DisplayField displayField = new DisplayField(false,true);
		displayField.setValue("<b>Filter:</b>");
		displayField.setVisible(false);
//		displayField.setMargin(Spacing.px(10));

		ListTreeModel<FacetNode<ENTITY>> filterTreeModel = new ListTreeModel<>(Collections.emptyList());
		Tree<FacetNode<ENTITY>> filterTree = new Tree<>(filterTreeModel);
		filterTree.setShowExpanders(false);
		filterTree.setOpenOnSelection(true);
		filterTree.setRecordToStringFunction(facetRecord -> "none");
		filterTree.setTemplateDecider(node -> node.facet().getTemplate());
		filterTree.setPropertyProvider((node, propertyNames) -> {
			PropertyProvider<Object> propertyProvider = (PropertyProvider<Object>) node.facet().getFacetPropertyProvider();
			return propertyProvider.getValues(node.facetRecord(), propertyNames);
		});
		filterTree.onNodeSelected.addListener((event) -> {
			filters.remove(event);
			filterTreeModel.setRecords(filters);
			onFilterChanged.fire();
		});

		onFilterChanged.addListener(() -> {
			displayField.setVisible(!filterTreeModel.getRecords().isEmpty());
		});

		ListTreeModel<FacetNode<ENTITY>> treeModel = new ListTreeModel<>(Collections.emptyList());
		treeModel.setTreeNodeInfoFunction(node -> new TreeNodeInfoImpl<>(node.parent(), node.facetRecord() == null && selectedFacets.contains(node.facet().getTitle()), node.facetRecord() != null));
		Tree<FacetNode<ENTITY>> tree = new Tree<>(treeModel);
		tree.setIndentation(0);
		tree.setCssStyle("overflow", "auto");
		tree.setRecordToStringFunction(facetRecord -> "none");
		tree.setShowExpanders(false);
		tree.setOpenOnSelection(true);
		Template facetHeaderTemplate = createFacetHeaderTemplate();

		tree.setTemplateDecider(node -> node.facetRecord() != null ? node.facet().getTemplate() : facetHeaderTemplate);
		tree.setPropertyProvider((node, propertyNames) -> {
			if (node.facetRecord() != null) {
				PropertyProvider<Object> propertyProvider = (PropertyProvider<Object>) node.facet().getFacetPropertyProvider();
				Map<String, Object> map = propertyProvider.getValues(node.facetRecord(), propertyNames);
				map.put(BaseTemplate.PROPERTY_BADGE, "" + getLocalizedFormatter().formatDecimalNumber(node.count()));
				return map;
			} else {
				return PropertyData.create(node.facet().getIcon(), node.facet().getTitle() + " (" + getLocalizedFormatter().formatDecimalNumber(node.getTotalFacets()) + ")");
			}
		});

		tree.onNodeExpansionChanged.addListener((event) -> {
			FacetNode<ENTITY> node = event.getNode();
			if (event.isExpanded()) {
				selectedFacets.add(node.facet().getTitle());
			} else {
				selectedFacets.remove(node.facet().getTitle());
			}
		});

		tree.onNodeSelected.addListener((node) -> {
			if (node.facetRecord() != null) {
				if (node.equals(lastSelectedNode) && System.currentTimeMillis() - lastSelection < 1000) {
					filters.remove(node);
					filters.add(node);
					filterTreeModel.setRecords(filters);
					onFilterChanged.fire();
				} else {
					onDataSelected.fire(node.records());
				}
				lastSelection = System.currentTimeMillis();
				lastSelectedNode = node;
			}
		});

		Consumer<List<ENTITY>> dataConsumer = (records) -> {
			List<FacetNode<ENTITY>> nodes = new ArrayList<>();
			for (DrillDownFacet<?, ENTITY> facet : facets) {
				FacetNode<ENTITY> parent = new FacetNode<>(facet);
				nodes.add(parent);
				Stream<ENTITY> recordStream = facet.getFacetFilter().apply(records.stream());
				if (facet.getFlatMapFunction() != null) {
					Map<Object, List<FacetPair<ENTITY>>> map = recordStream
							.flatMap(record -> facet.getFlatMapFunction().apply(record).map(e -> new FacetPair<>(record, e)))
							.filter(pair -> pair.facetRecord != null)
							.filter(pair -> facet.queryFacet(pair.facetRecord(), facetQuery))
							.collect(Collectors.groupingBy(FacetPair::facetRecord));
					parent.setTotalFacets(map.size());
					map.entrySet().stream()
							.filter(entry -> entry.getValue().size() >= facet.getMinGroupEntries())
							.sorted((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()))
							.limit(facet.getMaxFacetRecords())
							.forEach(entry -> {
								List<ENTITY> list = entry.getValue().stream().map(FacetPair::modelRecord).toList();
								nodes.add(new FacetNode<>(parent, facet, entry.getKey(), list, entry.getValue().size()));
							});
				} else {
					Map<?, List<ENTITY>> map = recordStream
							.filter(modelRecord -> facet.getGrouperFunction().apply(modelRecord) != null)
							.collect(Collectors.groupingBy(facet.getGrouperFunction()));
					parent.setTotalFacets(map.size());
					map.entrySet().stream()
							.filter(entry -> facet.queryFacet(entry.getKey(), facetQuery))
							.filter(entry -> entry.getValue().size() >= facet.getMinGroupEntries())
							.sorted((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()))
							.limit(facet.getMaxFacetRecords())
							.forEach(entry -> {
								nodes.add(new FacetNode<>(parent, facet, entry.getKey(), entry.getValue(), entry.getValue().size()));
							});
				}
			}
			treeModel.setRecords(nodes);
		};

		queryField.onTextInput.addListener((text) -> {
			if (text == null || text.isBlank()) {
				facetQuery = null;
			} else {
				facetQuery = text.toLowerCase();
			}
			dataConsumer.accept(drillDownModel.getFacetEntities().get());
		});

		drillDownModel.getFacetEntities().onChanged().addListener(records -> {
			if (isVisible()) {
				dataConsumer.accept(records);
			}
		});
		onViewRedrawRequired.addListener(() -> dataConsumer.accept(drillDownModel.getFacetEntities().get()));

		onDataSelected.addListener(drillDownModel::handleFacetSelection);
		onFilterChanged.addListener(() -> {
			if (filters.isEmpty()) {
				drillDownModel.handleFacetFilter(null);
			} else {
				drillDownModel.handleFacetFilter(this::filterRecords);
			}
		});

		verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(displayField);
		verticalLayout.addComponent(filterTree);
		verticalLayout.addComponentFillRemaining(tree);
		//addCloseButton(getParentPanel());

		getParentPanel().setRightHeaderField(queryField);
	}

	@Override
	public Component getViewComponent() {
		return verticalLayout;
	}

	public void closeFacets() {
		selectedFacets.clear();
		onFilterChanged.fire();
	}

	private void addCloseButton(Panel panel) {
		ToolButton toolButton = new ToolButton(ApplicationIcons.CLOSE);
		toolButton.setIconSize(16);
		toolButton.onClick.addListener(this::closeFacets);
		panel.addToolButton(toolButton);
	}

	public Stream<ENTITY> filterRecords(Stream<ENTITY> records) {
		Stream<ENTITY> filteredRecords = records;
		for (FacetNode<ENTITY> filter : filters) {
			Object facetRecord = filter.facetRecord();
			DrillDownFacet<?, ENTITY> facet = filter.facet();
			filteredRecords = facet.filter(facetRecord, filteredRecords);
		}
		return filteredRecords;
	}

	private Template createFacetHeaderTemplate() {
		String htmlTemplate = "<div style=\"display: flex;align-items: center;border-top: 1px solid " + Color.WHITE.toHtmlColorString() + ";border-bottom: 1px solid " + Color.MATERIAL_BLUE_700.withAlpha(0.2f).toHtmlColorString() + ";background-color:" + Color.MATERIAL_BLUE_700.withAlpha(0.05f).toHtmlColorString() + "\">\n" +
				"        <div class=\"icon img img-24\" style=\"background-image: url('{{icon}}');\"></div>\n" +
				"        <div>{{caption}}</div>\n" +
				"    </div>";
		return new MustacheTemplate(htmlTemplate);
	}

	public <FACET_RECORD> DrillDownFacet<FACET_RECORD, ENTITY> addFacetSimple(Icon icon, String title, Function<ENTITY, FACET_RECORD> grouperFunction, BiFunction<FACET_RECORD, Stream<ENTITY>, Stream<ENTITY>> filterHandler, Function<FACET_RECORD, String> captionFunction) {
		PropertyProvider<FACET_RECORD> facetPropertyProvider = (facetRecord, propertyNames) -> PropertyData.create(icon, captionFunction.apply(facetRecord));
		return addFacetDefaultTemplate(icon, title, grouperFunction, filterHandler, facetPropertyProvider);
	}

	public <FACET_RECORD> DrillDownFacet<FACET_RECORD, ENTITY> addFacetDefaultTemplate(Icon icon, String title, Function<ENTITY, FACET_RECORD> grouperFunction, BiFunction<FACET_RECORD, Stream<ENTITY>, Stream<ENTITY>> filterHandler, PropertyProvider<FACET_RECORD> facetPropertyProvider) {
		return addFacet(icon, title, grouperFunction, filterHandler, facetPropertyProvider, BaseTemplate.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE);
	}

	public <FACET_RECORD> DrillDownFacet<FACET_RECORD, ENTITY> addFacet(Icon icon, String title, Function<ENTITY, FACET_RECORD> grouperFunction, BiFunction<FACET_RECORD, Stream<ENTITY>, Stream<ENTITY>> filterHandler, PropertyProvider<FACET_RECORD> facetPropertyProvider, Template template) {
		DrillDownFacet<FACET_RECORD, ENTITY> drillDownFacet = new DrillDownFacet<>(icon, title, filterHandler, grouperFunction, null, facetPropertyProvider, template);
		facets.add(drillDownFacet);
		return drillDownFacet;
	}

	public <FACET_RECORD> DrillDownFacet<FACET_RECORD, ENTITY> addMultiReferenceFacet(Icon icon, String title, Function<ENTITY, Stream<FACET_RECORD>> flatMapFunction, BiFunction<FACET_RECORD, Stream<ENTITY>, Stream<ENTITY>> filterHandler, PropertyProvider<FACET_RECORD> facetPropertyProvider, Template template) {
		DrillDownFacet<FACET_RECORD, ENTITY> drillDownFacet = new DrillDownFacet<>(icon, title, filterHandler, null, flatMapFunction, facetPropertyProvider, template);
		facets.add(drillDownFacet);
		return drillDownFacet;
	}

	public void addMetaFieldFacets() {
		addMetaFieldFacets(TemporalFacetGrouper.MONTH);
	}

	public void addMetaFieldFacets(TemporalFacetGrouper grouper) {
		addUserIdMetaFacet(Table.FIELD_CREATED_BY);
		addUserIdMetaFacet(Table.FIELD_MODIFIED_BY);
		addInstantGrouper(getLocalized(Dictionary.CREATION_DATE), Table.FIELD_CREATION_DATE, grouper);
		addInstantGrouper(getLocalized(Dictionary.MODIFICATION_DATE), Table.FIELD_MODIFICATION_DATE, grouper);
	}



	public DrillDownFacet<Integer, ENTITY> addUserIdMetaFacet(String tableFieldName) {
		String title = switch (tableFieldName) {
			case Table.FIELD_CREATED_BY -> getLocalized(Dictionary.CREATED_BY);
			case Table.FIELD_MODIFIED_BY -> getLocalized(Dictionary.MODIFIED_BY);
			case Table.FIELD_DELETED_BY -> getLocalized(Dictionary.DELETED_BY);
			case Table.FIELD_RESTORED_BY -> getLocalized(Dictionary.RESTORED_BY);
			default -> null;
		};
		return addFacetDefaultTemplate(
				ApplicationIcons.USER,
				title,
				entity -> (int) entityPropertyFunction.apply(entity, tableFieldName),
				(userId, recordStream) -> recordStream.filter(entity -> (int) entityPropertyFunction.apply(entity, tableFieldName) == userId),
				getComponentFactory().createUserTemplateField().getPropertyProvider()
		).facetFilter(recordStream -> recordStream.filter(entity -> (int) entityPropertyFunction.apply(entity, tableFieldName) != 0));
	}

	public DrillDownFacet<String, ENTITY> addInstantGrouper(String title, String tableFieldName, TemporalFacetGrouper grouper) {
		Locale locale = getApplicationInstanceData().getUser().getLocale();
		return addFacetSimple(
				ApplicationIcons.CALENDAR_CLOCK,
				title,
				entity -> FacetUtils.getDateGroup(getInstant(entity, tableFieldName), grouper, locale),
				(s, recordStream) -> recordStream.filter(entity -> s.equals(FacetUtils.getDateGroup(getInstant(entity, tableFieldName), grouper, locale))),
				s -> s
		);
	}

	public DrillDownFacet<String, ENTITY> addIntGrouper(Icon<?,?> icon, String title, Function<ENTITY, Integer> numberFunction, int divisor) {
		return addFacetSimple(
				icon,
				title,
				entity -> FacetUtils.getLongGroup(numberFunction.apply(entity), divisor),
				(s, recordStream) -> recordStream.filter(entity -> s.equals(FacetUtils.getLongGroup(numberFunction.apply(entity), divisor))),
				s -> s
		);
	}

	public DrillDownFacet<String, ENTITY> addLongGrouper(Icon<?,?> icon, String title, Function<ENTITY, Long> numberFunction, int divisor) {
		return addFacetSimple(
				icon,
				title,
				entity -> FacetUtils.getLongGroup(numberFunction.apply(entity), divisor),
				(s, recordStream) -> recordStream.filter(entity -> s.equals(FacetUtils.getLongGroup(numberFunction.apply(entity), divisor))),
				s -> s
		);
	}

	public DrillDownFacet<Boolean, ENTITY> addBooleanFacet(Icon<?,?> icon, String title, String tableFieldName) {
		return addFacetDefaultTemplate(icon, title,
				entity -> (boolean) entityPropertyFunction.apply(entity, tableFieldName),
				(val, recordStream) -> recordStream.filter(entity -> (boolean) entityPropertyFunction.apply(entity, tableFieldName) == val),
				(aBoolean, propertyNames) -> {
					if (aBoolean) {
						return PropertyData.create(ApplicationIcons.CHECKBOX, title + ": " + getLocalized(Dictionary.YES));
					} else {
						return PropertyData.create(ApplicationIcons.CHECKBOX_UNCHECKED, title + ": " + getLocalized(Dictionary.NO));
					}
				}
		);
	}

	public DrillDownFacet<Boolean, ENTITY> addBooleanFacetWithPath(Icon<?,?> icon, String title, Function<ENTITY, Boolean> grouperFunction, BiFunction<Boolean, Stream<ENTITY>, Stream<ENTITY>> filterHandler) {
		return addFacetDefaultTemplate(icon, title,
				grouperFunction, filterHandler, (aBoolean, propertyNames) -> {
					if (aBoolean) {
						return PropertyData.create(ApplicationIcons.CHECKBOX, title + ": " + getLocalized(Dictionary.YES));
					} else {
						return PropertyData.create(ApplicationIcons.CHECKBOX_UNCHECKED, title + ": " + getLocalized(Dictionary.NO));
					}
				}
		);
	}

	private Instant getInstant(ENTITY entity, String tableFieldName) {
		Object entityValue =  entityPropertyFunction.apply(entity, tableFieldName);
		if (entityValue instanceof Integer value) {
			return Instant.ofEpochSecond(value);
		}
		return null;
	}

}
