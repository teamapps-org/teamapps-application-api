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
package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public interface SimplePrivilege extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.SIMPLE_PRIVILEGE;
	}

	@Override
	default List<Privilege> getPrivileges() {
		return Collections.emptyList();
	}

	@Override
	default Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier() {
		return null;
	}
}
