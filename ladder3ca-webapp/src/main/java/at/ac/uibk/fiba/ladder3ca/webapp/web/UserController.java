package at.ac.uibk.fiba.ladder3ca.webapp.web;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.AuditLogDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.PasswordChangeRequestDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.SimpleMessageDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/changePassword")
    public SimpleMessageDTO changePassword(@RequestBody PasswordChangeRequestDTO req) {
        return userService.changePassword(req);
    }

    @GetMapping("/logs")
    public List<AuditLogDTO> getLogsByUser() {
        return userService.getLogs();
    }
}
