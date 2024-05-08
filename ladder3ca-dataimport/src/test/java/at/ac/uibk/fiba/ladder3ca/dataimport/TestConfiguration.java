package at.ac.uibk.fiba.ladder3ca.dataimport;

import at.ac.uibk.fiba.ficker.docx2tei.Docx2TeiConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import({DataImportConfig.class, Docx2TeiConfiguration.class})
public class TestConfiguration {

}
