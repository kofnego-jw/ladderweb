package at.ac.uibk.fiba.ladder3;

import at.ac.uibk.fiba.ficker.docx2tei.Docx2TeiConfiguration;
import at.ac.uibk.fiba.ladder3.repository.Ladder3RepositoryService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.File;

@SpringBootConfiguration
@ComponentScan
@Import({Docx2TeiConfiguration.class})
public class TestConfiguration {

    @Bean
    public Ladder3RepositoryService repositoryService() {
        return new Ladder3RepositoryService(new File("materials/repo.json"));
    }

}
