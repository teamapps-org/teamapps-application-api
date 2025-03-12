/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
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
package org.teamapps.application.api.application;

import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLazyRenderingApplicationView extends AbstractApplicationView {

	public final Event<Void> onViewRedrawRequired = new Event<>();
	private final TwoWayBindableValue<Boolean> visible = TwoWayBindableValue.create(false);
	private View parentView;
	private Window parentWindow;
	private Panel parentPanel;
	private View targetView;
	private AbstractLazyRenderingApplicationView targetApplicationView;
	private Window targetWindow;
	protected boolean created;
	private List<AbstractLazyRenderingApplicationView> peerViewsToHideWhenVisible = Collections.emptyList();
	private List<AbstractLazyRenderingApplicationView> peerViewsToShowWhenVisible = Collections.emptyList();
	private List<AbstractLazyRenderingApplicationView> peerWithSameParent = Collections.emptyList();
	private ViewSize ensureViewSize;
	private List<ToolbarButtonGroup> buttonGroups = new ArrayList<>();

	public AbstractLazyRenderingApplicationView(ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		visible.onChanged().addListener(isVisible -> {
			if (isVisible) {
				onViewRedrawRequired.fire();
			}
		});
	}

	public AbstractLazyRenderingApplicationView setParentView(View parent) {
		this.parentView = parent;
		return this;
	}

	public AbstractLazyRenderingApplicationView setParentWindow(Window parent) {
		this.parentWindow = parent;
		this.parentPanel = parent;
		return this;
	}

	public AbstractLazyRenderingApplicationView setParentPanel(Panel parent) {
		this.parentPanel = parent;
		return this;
	}

	public AbstractLazyRenderingApplicationView setTargetView(View target) {
		this.targetView = target;
		return this;
	}

	public AbstractLazyRenderingApplicationView setTargetView(AbstractLazyRenderingApplicationView targetApplicationView) {
		this.targetApplicationView = targetApplicationView;
		return this;
	}

	public AbstractLazyRenderingApplicationView setTargetWindow(Window target) {
		this.targetWindow = target;
		return this;
	}

	public AbstractLazyRenderingApplicationView setPeerViews(List<AbstractLazyRenderingApplicationView> allViews, AbstractLazyRenderingApplicationView... peerViews) {
		return setPeerViews(allViews, Collections.emptyList(), peerViews);
	}

	public AbstractLazyRenderingApplicationView setPeerViews(List<AbstractLazyRenderingApplicationView> allViews, List<AbstractLazyRenderingApplicationView> exclude, AbstractLazyRenderingApplicationView... peerViews) {
		peerViewsToShowWhenVisible = Arrays.stream(peerViews).filter(v -> !v.equals(this)).collect(Collectors.toList());
		peerViewsToHideWhenVisible = allViews.stream().filter(v -> !exclude.contains(v) && !peerViewsToShowWhenVisible.contains(v) && !v.equals(this)).collect(Collectors.toList());
		return this;
	}

	public AbstractLazyRenderingApplicationView setPeerViewsToHideWhenVisible(AbstractLazyRenderingApplicationView... views) {
		peerViewsToHideWhenVisible = Arrays.stream(views).filter(v -> !v.equals(this)).collect(Collectors.toList());
		return this;
	}

	public AbstractLazyRenderingApplicationView setPeerViewsToShowWhenVisible(AbstractLazyRenderingApplicationView... views) {
		peerViewsToShowWhenVisible = Arrays.stream(views).filter(v -> !v.equals(this)).collect(Collectors.toList());
		return this;
	}

	public AbstractLazyRenderingApplicationView setPeersWithSameParent(AbstractLazyRenderingApplicationView... views) {
		peerWithSameParent = Arrays.stream(views).filter(v -> !v.equals(this)).collect(Collectors.toList());
		return this;
	}

	public abstract void createUi();

	public abstract Component getViewComponent();


	protected void handleViewComponentChange() {
		if (parentView != null) {
			parentView.setComponent(getViewComponent());
		} else if (parentWindow != null) {
			parentWindow.setContent(getViewComponent());
		} else if (parentPanel != null) {
			parentPanel.setContent(getViewComponent());
		}
	}

	public void handleModelDataChanged() {
		if (isVisible()) {
			onViewRedrawRequired.fire();
		}
	}

	public void show() {
		show(true);
	}

	public void show(boolean select) {
		buttonGroups.forEach(bg -> bg.setVisible(true));
		peerWithSameParent.forEach(AbstractLazyRenderingApplicationView::unsetView);
		if (parentView != null && ensureViewSize != null) {
			parentView.setSize(ensureViewSize);
		}
		if (!created) {
			createUi();
			created = true;
		}
		if (parentView != null) {
			if (parentView.getComponent() == null || !parentView.getComponent().equals(getViewComponent())) {
				parentView.setComponent(getViewComponent());
			}
			parentView.setVisible(true);
			if (select) {
				parentView.focus();
			}
		} else if (parentPanel != null) {
			if (parentPanel.getContent() == null || !parentPanel.getContent().equals(getViewComponent())) {
				parentPanel.setContent(getViewComponent());
			}
			if (parentWindow != null) {
				parentWindow.show();
			}
		} else if (parentWindow != null) {
			parentWindow.setContent(getViewComponent());
			parentWindow.show();
		}
		visible.set(true);
		peerViewsToHideWhenVisible.forEach(v -> {
			if (v.isVisible()) {
				v.hide();
			}
		});
		peerViewsToShowWhenVisible.forEach(v -> {
			if (!v.isVisible()) {
				v.show(false);
			}
		});
	}

	public void unsetView() {
		buttonGroups.forEach(bg -> bg.setVisible(false));
	}


	public void hide() {
		visible.set(false);
		if (parentView != null) {
			parentView.setVisible(false);
		} else if (parentWindow != null) {
			parentWindow.close();
		}
	}

	public void focusTargetView() {
		if (targetApplicationView != null) {
			targetApplicationView.show(true);
		}
		if (targetView != null) {
			targetView.focus();
		} else if (targetWindow != null) {
			targetWindow.show();
		}
	}

	public boolean isVisible() {
		return visible.get();
	}

	public ObservableValue<Boolean> getVisible() {
		return visible;
	}

	public Panel getParentPanel() {
		if (parentView != null) {
			return parentView.getPanel();
		} else if (parentPanel != null) {
			return parentPanel;
		} else if (parentWindow != null) {
			return parentWindow;
		}
		return null;
	}

	public ToolbarButtonGroup createToolbarButtonGroup() {
		return createToolbarButtonGroup(false, new ToolbarButtonGroup());
	}

	public ToolbarButtonGroup createToolbarButtonGroup(boolean local) {
		return createToolbarButtonGroup(local, new ToolbarButtonGroup());
	}

	public ToolbarButtonGroup createToolbarButtonGroup(boolean local, ToolbarButtonGroup buttonGroup) {
		ToolbarButtonGroup group = buttonGroup != null ? buttonGroup : new ToolbarButtonGroup();
		buttonGroups.add(group);
		if (parentView != null) {
			if (local) {
				return parentView.addLocalButtonGroup(group);
			} else {
				return parentView.addWorkspaceButtonGroup(group);
			}
		} else if (parentPanel != null) {
			if (parentPanel.getToolbar() == null) {
				parentPanel.setToolbar(new Toolbar());
			}
			return parentPanel.getToolbar().addButtonGroup(group);
		} else if (parentWindow != null) {
			if (parentWindow.getToolbar() == null) {
				parentWindow.setToolbar(new Toolbar());
			}
			return parentWindow.getToolbar().addButtonGroup(group);
		}
		return null;
	}

	public View getTargetView() {
		return targetView;
	}

	public void setEnsureViewSize(ViewSize ensureViewSize) {
		this.ensureViewSize = ensureViewSize;
	}
}
