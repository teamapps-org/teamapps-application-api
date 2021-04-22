package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRoleBuilder {
    private List<ApplicationRole> roles = new ArrayList<>();

    public ApplicationRole addRole(String name, Icon<?,?> icon, String titleKey, String descriptionKey, List<PrivilegeGroup> privilegeGroups) {
        ApplicationRole role = new ApplicationRoleImpl(name, icon, titleKey, descriptionKey, privilegeGroups);
        this.addRole(role);
        return role;
    }

    private ApplicationRole addRole(ApplicationRole role) {
        this.roles.add(role);
        return role;
    }

    public List<ApplicationRole> getRoles() {
        return this.roles;
    }
}
