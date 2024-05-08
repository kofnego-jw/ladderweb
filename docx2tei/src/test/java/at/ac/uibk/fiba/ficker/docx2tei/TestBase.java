package at.ac.uibk.fiba.ficker.docx2tei;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {Docx2TeiConfiguration.class})
public abstract class TestBase {
}
