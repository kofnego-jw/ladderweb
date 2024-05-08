package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import at.ac.uibk.fiba.ladder3ca.business.security.AppUserService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AppUserDTO {

    public final Long id;
    public final String emailAddress;
    public final List<AppRole> roles;

    public AppUserDTO(Long id, String emailAddress, List<AppRole> roles) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.roles = roles;
    }

    public static AppUserDTO fromAppUser(AppUser user) {
        if (user == null) {
            return null;
        }
        return new AppUserDTO(user.getId(), user.getEmailAddress(), AppUserService.stringToRoles(user.getRoleString()));
    }

    public static List<AppUserDTO> fromAppUsers(List<AppUser> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(AppUserDTO::fromAppUser).collect(Collectors.toList());
    }
}
