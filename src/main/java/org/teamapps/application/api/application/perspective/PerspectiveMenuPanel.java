/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.api.application.perspective;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.model.ListTreeModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerspectiveMenuPanel {

	private final ApplicationInstanceData applicationInstanceData;
	private final List<PerspectiveBuilder> perspectiveBuilders;
	private Tree<PerspectiveBuilder> tree;
	private SimpleItemView<PerspectiveBuilder> buttonMenu;
	private Map<PerspectiveBuilder, ApplicationPerspective> perspectiveByBuilderMap;

	public static PerspectiveMenuPanel createMenuPanel(ApplicationInstanceData applicationInstanceData, PerspectiveBuilder... perspectiveBuilders) {
		return createMenuPanel(applicationInstanceData, Arrays.asList(perspectiveBuilders));
	}

	public static PerspectiveMenuPanel createMenuPanel(ApplicationInstanceData applicationInstanceData, List<PerspectiveBuilder> perspectiveBuilders) {
		return new PerspectiveMenuPanel(applicationInstanceData, perspectiveBuilders);
	}

	public PerspectiveMenuPanel(ApplicationInstanceData applicationInstanceData, List<PerspectiveBuilder> perspectiveBuilders) {
		this.applicationInstanceData = applicationInstanceData;
		this.perspectiveBuilders = perspectiveBuilders;
		init();
	}

	private void init() {
		perspectiveByBuilderMap = new HashMap<>();
		List<PerspectiveBuilder> allowedPerspectiveBuilders = perspectiveBuilders.stream().filter(p -> p.isPerspectiveAccessible(applicationInstanceData)).collect(Collectors.toList());
		createButtonMenu(allowedPerspectiveBuilders);
		createTree(allowedPerspectiveBuilders);
	}

	private void createButtonMenu(List<PerspectiveBuilder> allowedPerspectiveBuilders) {
		buttonMenu = new SimpleItemView<>();
		SimpleItemGroup<PerspectiveBuilder> itemGroup = buttonMenu.addSingleColumnGroup(ApplicationIcons.WINDOW_EXPLORER, applicationInstanceData.getLocalized(Dictionary.VIEWS));
		itemGroup.setItemTemplate(BaseTemplate.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES);
		allowedPerspectiveBuilders.forEach(builder -> {
			itemGroup
					.addItem(builder.getIcon(), applicationInstanceData.getLocalized(builder.getTitleKey()), applicationInstanceData.getLocalized(builder.getDescriptionKey()))
					.onClick.addListener(() -> openPerspective(builder));
		});
	}

	private void createTree(List<PerspectiveBuilder> allowedPerspectiveBuilders) {
		ListTreeModel<PerspectiveBuilder> treeModel = new ListTreeModel<>(allowedPerspectiveBuilders);
		tree = new Tree<>(treeModel);
		tree.setShowExpanders(false);
		tree.setEntryTemplate(BaseTemplate.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES);
		tree.setPropertyExtractor((builder, propertyName) -> switch (propertyName) {
			case BaseTemplate.PROPERTY_ICON -> builder.getIcon();
			case BaseTemplate.PROPERTY_CAPTION -> applicationInstanceData.getLocalized(builder.getTitleKey());
			case BaseTemplate.PROPERTY_DESCRIPTION -> applicationInstanceData.getLocalized(builder.getDescriptionKey());
			default -> null;
		});
		tree.onNodeSelected.addListener(this::openPerspective);
		if (!treeModel.getRecords().isEmpty()) {
			tree.setSelectedNode(treeModel.getRecords().get(0));
		}
	}

	public void openPerspective(PerspectiveBuilder builder) {
		ApplicationPerspective applicationPerspective = perspectiveByBuilderMap.get(builder);
		if (applicationPerspective == null) {
			applicationPerspective = builder.build(applicationInstanceData, null);
			perspectiveByBuilderMap.put(builder, applicationPerspective);
		}
		tree.setSelectedNode(builder);
		applicationInstanceData.showPerspective(applicationPerspective.getPerspective());
	}

	public void addInstantiatedPerspective(PerspectiveBuilder builder, ApplicationPerspective perspective) {
		perspectiveByBuilderMap.put(builder, perspective);
		if (tree != null) {
			tree.setSelectedNode(tree.getModel().getRecords().stream().filter(record -> record.equals(builder)).findAny().orElse(null));
		}
	}

	public Component getComponent() {
		return tree;
	}

	public SimpleItemView<PerspectiveBuilder> getButtonMenu() {
		return buttonMenu;
	}
}
