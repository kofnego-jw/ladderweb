package at.ac.uibk.fiba.ladder3ca.business.security;

import org.springframework.security.core.GrantedAuthority;

public enum AppRole implements GrantedAuthority {

    ADMIN, USER;


    @Override
    public String getAuthority() {
        return this.name();
    }
}
