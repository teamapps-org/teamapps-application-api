package org.teamapps.application.api.event;

import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;

public class TwoWayBindableValueFireAlways<T> implements TwoWayBindableValue<T> {

	public final Event<T> onChanged = new Event<>();

	private T value;

	public static <T> TwoWayBindableValue<T> create() {
		return new TwoWayBindableValueFireAlways<>();
	}

	public TwoWayBindableValueFireAlways() {
	}

	public TwoWayBindableValueFireAlways(T initialValue) {
		this.value = initialValue;
	}

	@Override
	public Event<T> onChanged() {
		return onChanged;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		this.value = value;
		onChanged.fire(value);
	}
}