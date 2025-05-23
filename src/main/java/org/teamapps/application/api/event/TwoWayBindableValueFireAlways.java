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
package org.teamapps.application.api.event;

import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;

public class TwoWayBindableValueFireAlways<T> implements TwoWayBindableValue<T> {

	public final Event<T> onChanged = new Event<>();

	private T value;

	public static <T> TwoWayBindableValue<T> create() {
		return new TwoWayBindableValueFireAlways<>();
	}

	public static <T> TwoWayBindableValue<T> create(T initialValue) {
		return new TwoWayBindableValueFireAlways<>(initialValue);
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
