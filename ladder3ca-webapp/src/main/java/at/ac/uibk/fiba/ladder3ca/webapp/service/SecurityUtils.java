package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import at.ac.uibk.fiba.ladder3ca.business.security.AppUserDetails;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    public static AppUser getAppUser() {
        AppUserDetails principal = getPrincipal();
        return principal == null ? null : principal.appUser;
    }

    public static AppUserDetails getPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Object principal = context.getAuthentication().getPrincipal();
            return principal instanceof AppUserDetails ? (AppUserDetails) principal : null;
        }
        return null;
    }

    public static void throwUnauthorized() {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }


    public static void needAdmin() {
        if (getPrincipal() != null && getPrincipal().hasAuthority(AppRole.ADMIN)) {
            return;
        }
        throwUnauthorized();
    }

    public static boolean isAdmin() {
        if (getPrincipal() == null) {
            return false;
        }
        return getPrincipal().hasAuthority(AppRole.ADMIN);
    }

    public static boolean isUser() {
        if (getPrincipal() == null) {
            return false;
        }
        return isAdmin() || getPrincipal().hasAuthority(AppRole.USER);
    }

    public static void needUserOrAdmin() {
        if (getPrincipal() != null && (getPrincipal().hasAuthority(AppRole.ADMIN) || getPrincipal().hasAuthority(AppRole.USER))) {
            return;
        }
        throwUnauthorized();
    }
}
