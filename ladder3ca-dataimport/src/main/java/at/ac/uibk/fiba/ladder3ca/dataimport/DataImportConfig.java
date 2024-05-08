package at.ac.uibk.fiba.ladder3ca.dataimport;

import at.ac.uibk.fiba.ladder3ca.webapp.WebappConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import({WebappConfig.class})
@ComponentScan
@EnableAutoConfiguration
public class DataImportConfig {

}
