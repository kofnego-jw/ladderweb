package at.ac.uibk.fiba.ladder3ca.webapp.web;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import at.ac.uibk.fiba.ladder3ca.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public List<AppUserDTO> listAllUsers() {
        return userService.listAllUsers();
    }

    @PostMapping("/user")
    public SimpleMessageDTO createUser(@RequestBody CreateUserRequestDTO req) {
        return userService.createUser(req);
    }

    @PostMapping("/user/update")
    public SimpleMessageDTO updateUser(@RequestBody UpdateUserRequestDTO req) {
        return userService.updateUser(req);
    }

    @PostMapping("/user/password")
    public SimpleMessageDTO changePassword(@RequestBody PasswordChangeRequestDTO req) {
        return userService.changePassword(req);
    }

    @GetMapping("/logs")
    public List<AuditLogDTO> getLogs() {
        return userService.getLogs();
    }

}
