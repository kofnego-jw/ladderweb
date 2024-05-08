package at.ac.uibk.fiba.ladder3ca.business.security;

import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.AppUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AppUserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final AppUserRepository appUserRepository;

    private final AuditLogService logService;

    public AppUserService(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository, AuditLogService logService) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.logService = logService;
    }

    public static String rolesToString(List<AppRole> roles) {
        if (roles == null) {
            return "";
        }
        return roles.stream().map(x -> x.name()).collect(Collectors.joining(";"));
    }

    public static List<AppRole> stringToRoles(String s) {
        if (StringUtils.isBlank(s)) {
            return Collections.emptyList();
        }
        String[] split = s.split(";");
        return Stream.of(split).map(x -> AppRole.valueOf(x)).collect(Collectors.toList());
    }

    private String hashPassword(String rawPassword) {
        return this.passwordEncoder.encode(rawPassword);
    }

    public List<AppUser> listAll() {
        return this.appUserRepository.findAll();
    }

    public AppUser create(String email, String rawPassword, List<AppRole> roles, AppUser byUser) {
        Optional<AppUser> inDbOpt = appUserRepository.findByEmail(email);
        if (inDbOpt.isPresent()) {
            throw new IllegalArgumentException("A user with this email aldready exists.");
        }
        AppUser user = new AppUser(email, hashPassword(rawPassword), rolesToString(roles));
        appUserRepository.save(user);
        logService.log("User '" + email + "' created.", byUser);
        return user;
    }

    public AppUser update(Long userId, String email, List<AppRole> roles, AppUser user) {
        if (userId == null) {
            throw new IllegalArgumentException("Cannot update user without id.");
        }
        Optional<AppUser> userOpt = appUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Cannot find user in db.");
        }
        AppUser toUpdate = userOpt.get();
        toUpdate.setEmailAddress(email);
        toUpdate.setRoleString(rolesToString(roles));
        appUserRepository.save(toUpdate);
        logService.log("User '" + email + "' updated.", user);
        return toUpdate;
    }

    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    public AppUser changePassword(Long userId, String rawPassword, AppUser user) {
        Optional<AppUser> userOpt = appUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }
        AppUser appUser = userOpt.get();
        appUser.setPasswordHash(hashPassword(rawPassword));
        logService.log("Password changed for User " + appUser.getId(), user);
        return appUserRepository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AppUser user = findByEmail(s);
        if (user == null) {
            throw new UsernameNotFoundException("Cannot find user.");
        }
        return new AppUserDetails(user);
    }
}
