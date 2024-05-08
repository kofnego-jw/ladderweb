package at.ac.uibk.fiba.ladder3ca.server;

import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import at.ac.uibk.fiba.ladder3ca.business.security.AppUserService;
import at.ac.uibk.fiba.ladder3ca.webapp.WebappConfig;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.CreationTaskDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.service.InitService;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Import({WebappConfig.class})
@EnableScheduling
public class Ladder3CaServer {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(Ladder3CaServer.class);
        // Add default user
        AppUserService userService = ctx.getBean(AppUserService.class);
        if (userService.listAll().isEmpty()) {
            userService.create("joseph.wang@uibk.ac.at", "changeMe!", List.of(AppRole.ADMIN, AppRole.USER), null);
        }
        // Add new data if any
        File workingDir = ctx.getBean("workingDir", File.class);
        File ingested = new File(workingDir, "ingestedRepo.txt");
        List<String> ingestedFiles = new ArrayList<>();
        try {
            InitService initService = ctx.getBean(InitService.class);
            String s = FileUtils.readFileToString(ingested, StandardCharsets.UTF_8);
            if (s != null) {
                String[] parts = s.split("\\r?\\n");
                ingestedFiles.addAll(Arrays.asList(parts));
            }
            if (!ingestedFiles.contains("repo.json")) {
                File repo = new File(workingDir, "ladder3data/repo.json");
                System.out.println("Adding data from: " + repo.getName());
                initService.addData(repo, new CreationTaskDTO(null, "Cancellation - Dinner", "Description of Cancellation task", "Cancellation"));
                ingestedFiles.add("repo.json");
            }
            if (!ingestedFiles.contains("ITA_Dinner_2021.json")) {
                File repo = new File(workingDir, "ladder3data/ITA_Dinner_2021.json");
                System.out.println("Adding data from: " + repo.getName());
                initService.addData(repo, new CreationTaskDTO(null, "Cancellation - DirDis Dinner",
                        "Desc of task ", "Cancellation"));
                ingestedFiles.add("ITA_Dinner_2021.json");
            }
            if (!ingestedFiles.contains("ITA_Drink_2021.json")) {
                File repo = new File("./workingDir/ladder3data/ITA_Drink_2021.json");
                System.out.println("Adding data from: " + repo.getName());
                initService.addData(repo, new CreationTaskDTO(null, "Cancellation - DisDir Drink",
                        "Description of this cancellation act", "Cancellation"));
                ingestedFiles.add("ITA_Drink_2021.json");
            }
            if (!ingestedFiles.contains("ITA_Party_2021.json")) {
                File repo = new File("./workingDir/ladder3data/ITA_Party_2021.json");
                System.out.println("Adding data from: " + repo.getName());
                initService.addData(repo, new CreationTaskDTO(null, "Cancellation - DisDir Party",
                        "Description of this cancellation act", "Cancellation"));
                ingestedFiles.add("ITA_Party_2021.json");
            }
            if (!ingestedFiles.contains("ITA_L1L2_Request.json")) {
                File repo = new File("./workingDir/ladder3data/ITA_L1L2_Request.json");
                System.out.println("Adding data from: " + repo.getName());
                initService.addData(repo, new CreationTaskDTO(null, "Request - L1/L2", "Comparison L1 L2", "Request"));
                ingestedFiles.add("ITA_L1L2_Request.json");
            }
            // initService.retrainAllModels();
        } finally {
            String collected = String.join("\n", ingestedFiles);
            FileUtils.writeStringToFile(ingested, collected, StandardCharsets.UTF_8);
        }

        System.out.println("Ladder 3 Clariah App running with " + ctx.getBeanDefinitionCount() + " beans.");
    }

}
