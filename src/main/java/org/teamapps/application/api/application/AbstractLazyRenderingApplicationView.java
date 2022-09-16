package org.teamapps.application.api.application;

import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLazyRenderingApplicationView extends AbstractApplicationView {

	public final Event<Void> onViewRedrawRequired = new Event<>();
	private final TwoWayBindableValue<Boolean> visible = TwoWayBindableValue.create(true);
	private final View view;
	private final View targetView;
	private boolean created;
	private List<AbstractLazyRenderingApplicationView> viewPeers = Collections.emptyList();

	public AbstractLazyRenderingApplicationView(View view, View targetView, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.view = view;
		this.targetView = targetView;
		visible.onChanged().addListener(isVisible -> {
			if (isVisible) {
				onViewRedrawRequired.fire();
			}
		});
	}

	public void setPeerViews(AbstractLazyRenderingApplicationView... views) {
		viewPeers = Arrays.asList(views).stream().filter(v -> !v.equals(this)).collect(Collectors.toList());
	}

	public abstract void createUi();

	public abstract Component getViewComponent();

	public void show(boolean select) {
		viewPeers.forEach(v -> v.visible.set(false));
		visible.set(true);
		if (!created) {
			long time = System.currentTimeMillis();
			createUi();
			System.out.println("Render " + this.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - time));
			created = true;
		}
		view.setComponent(getViewComponent());
		view.setVisible(true);
		if (select) {
			view.focus();
		}
	}

	public void hide() {
		visible.set(false);
		view.setVisible(false);
		viewPeers.forEach(v -> v.visible.set(false));
	}

	public void focusTargetView() {
		if (targetView != null) {
			targetView.focus();
		}
	}

	public boolean isVisible() {
		return visible.get();
	}

	public View getView() {
		return view;
	}

	public View getTargetView() {
		return targetView;
	}
}
