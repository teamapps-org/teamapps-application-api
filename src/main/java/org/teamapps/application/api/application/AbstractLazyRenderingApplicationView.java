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
package org.teamapps.application.api.application;

import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.window.Window;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLazyRenderingApplicationView extends AbstractApplicationView {

	public final Event<Void> onViewRedrawRequired = new Event<>();
	private final TwoWayBindableValue<Boolean> visible = TwoWayBindableValue.create(true);
	private View parentView;
	private Window parentWindow;
	private Panel parentPanel;
	private View targetView;
	private Window targetWindow;
	private boolean created;
	private List<AbstractLazyRenderingApplicationView> peerViewsToHideWhenVisible = Collections.emptyList();
	private List<AbstractLazyRenderingApplicationView> peerViewsToShowWhenVisible = Collections.emptyList();
	private ViewSize ensureViewSize;

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

	public AbstractLazyRenderingApplicationView setTargetWindow(Window target) {
		this.targetWindow = target;
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

	public abstract void createUi();

	public abstract Component getViewComponent();

	public void handleModelDataChanged() {
		if (isVisible()) {
			onViewRedrawRequired.fire();
		}
	}

	public void show(boolean select) {
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
		} else if (parentWindow != null) {
			if (parentWindow.getContent() == null || !parentWindow.getContent().equals(getViewComponent())) {
				parentWindow.setContent(getViewComponent());
			}
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

	public void hide() {
		visible.set(false);
		if (parentView != null) {
			parentView.setVisible(false);
		} else if (parentWindow != null) {
			parentWindow.close();
		}
	}

	public void focusTargetView() {
		if (targetView != null) {
			targetView.focus();
		} else if (targetWindow != null) {
			targetWindow.show();
		}
	}

	public boolean isVisible() {
		return visible.get();
	}

	public Panel getParentPanel() {
		if (parentView != null) {
			return parentView.getPanel();
		} else if (parentPanel != null) {
			return parentPanel;
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
		if (parentView != null) {
			if (local) {
				return parentView.addLocalButtonGroup(new ToolbarButtonGroup());
			} else {
				return parentView.addWorkspaceButtonGroup(new ToolbarButtonGroup());
			}
		} else if (parentPanel != null) {
			if (parentPanel.getToolbar() == null) {
				parentPanel.setToolbar(new Toolbar());
			}
			return parentPanel.getToolbar().addButtonGroup(new ToolbarButtonGroup());
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
