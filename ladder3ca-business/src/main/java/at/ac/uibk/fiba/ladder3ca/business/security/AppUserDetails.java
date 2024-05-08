package at.ac.uibk.fiba.ladder3ca.business.security;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AppUserDetails implements UserDetails {

    public final AppUser appUser;

    public AppUserDetails(AppUser appUser) {
        this.appUser = appUser;
    }

    public boolean hasAuthority(AppRole role) {
        if (getAuthorities() == null || role == null) {
            return false;
        }
        return getAuthorities().contains(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AppUserService.stringToRoles(this.appUser.getRoleString());
    }

    @Override
    public String getPassword() {
        return this.appUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return this.appUser.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
