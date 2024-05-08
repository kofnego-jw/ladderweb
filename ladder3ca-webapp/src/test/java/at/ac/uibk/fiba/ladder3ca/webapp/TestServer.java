package at.ac.uibk.fiba.ladder3ca.webapp;

import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import at.ac.uibk.fiba.ladder3ca.business.security.AppUserService;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.CreationTaskDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.service.InitService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.List;

public class TestServer {

    public static final File DATA_DIR = new File("site/ladder3data");

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(WebappConfig.class);
        InitService initService = ctx.getBean(InitService.class);
        File repo = new File(DATA_DIR, "repo.json");
        initService.addData(repo, new CreationTaskDTO(null, "Cancellation", "Cancellation via SMS", "cancellation"));
        AppUserService userService = ctx.getBean(AppUserService.class);
        userService.create("test@example.com", "123", List.of(AppRole.ADMIN, AppRole.USER), null);
    }

}
