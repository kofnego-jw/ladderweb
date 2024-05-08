package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import at.ac.uibk.fiba.ladder3ca.business.security.AppUserService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AuditLog;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService logService;

    public UserService(AppUserService appUserService, PasswordEncoder passwordEncoder, AuditLogService logService) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    private static List<AppRole> stringListToRoles(List<String> roles) {
        return roles.stream().map(r -> {
            try {
                return AppRole.valueOf(r.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public SimpleMessageDTO createUser(CreateUserRequestDTO req) {
        SecurityUtils.needAdmin();
        List<AppRole> roles = stringListToRoles(req.roles);
        try {
            AppUser appUser = appUserService.create(req.emailAddress, req.password, roles, SecurityUtils.getAppUser());
            return new SimpleMessageDTO(HttpStatus.OK.value(), "User '" + appUser.getEmailAddress() + "' (ID " +
                    appUser.getId() + ") created", Collections.emptyList());
        } catch (Exception e) {
            return new SimpleMessageDTO(HttpStatus.BAD_REQUEST.value(), "Cannot create user.", List.of(e.getMessage()));
        }
    }

    public SimpleMessageDTO updateUser(UpdateUserRequestDTO dto) {
        SecurityUtils.needAdmin();
        List<AppRole> roles = stringListToRoles(dto.roles);
        try {
            AppUser update = appUserService.update(dto.userId, dto.emailAddress, roles, SecurityUtils.getAppUser());
            return new SimpleMessageDTO(HttpStatus.OK.value(), "User (ID " + update.getId() + ") updated.", Collections.emptyList());
        } catch (Exception e) {
            return new SimpleMessageDTO(HttpStatus.BAD_REQUEST.value(), "Cannot update user.", List.of(e.getMessage()));
        }
    }

    public SimpleMessageDTO changePassword(PasswordChangeRequestDTO req) {
        AppUser user = SecurityUtils.getAppUser();
        if (user == null) {
            SecurityUtils.throwUnauthorized();
            return null;
        }
        if (SecurityUtils.isAdmin()) {
            appUserService.changePassword(req.userId, req.newPassword, user);
        } else {
            if (!user.getId().equals(req.userId) || !passwordEncoder.matches(req.oldPassword, user.getPasswordHash())) {
                SecurityUtils.throwUnauthorized();
                return null;
            }
            appUserService.changePassword(req.userId, req.newPassword, user);
        }
        return new SimpleMessageDTO(HttpStatus.OK.value(), "Password changed.", Collections.emptyList());
    }

    public List<AuditLogDTO> getLogs() {
        if (SecurityUtils.isAdmin()) {
            List<AuditLog> logs = logService.getLogsSince(LocalDateTime.now().minusDays(90L));
            return AuditLogDTO.fromAuditLogs(logs);
        }
        AppUser user = SecurityUtils.getAppUser();
        if (user == null) {
            SecurityUtils.throwUnauthorized();
            return Collections.emptyList();
        }
        List<AuditLog> logs = logService.getLogByUser(user);
        return AuditLogDTO.fromAuditLogs(logs);
    }

    public List<AppUserDTO> listAllUsers() {
        List<AppUser> all = appUserService.listAll();
        return AppUserDTO.fromAppUsers(all);
    }

    public AppUserDTO getActiveUser() {
        AppUser user = SecurityUtils.getAppUser();
        return AppUserDTO.fromAppUser(user);
    }
}
