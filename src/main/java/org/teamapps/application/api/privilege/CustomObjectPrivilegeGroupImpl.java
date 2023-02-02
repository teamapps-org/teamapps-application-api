/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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
package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CustomObjectPrivilegeGroupImpl extends AbstractPrivilegeGroup implements CustomObjectPrivilegeGroup{

	public CustomObjectPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		super(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
	}

	@Override
	public PrivilegeGroup createCopyWithPrivileges(Privilege... privileges) {
		return new CustomObjectPrivilegeGroupImpl(getName(), getIcon(), getTitleKey(), getDescriptionKey(), Arrays.asList(privileges), getPrivilegeObjectsSupplier());
	}
}
