package at.ac.uibk.fiba.ladder3ca.dataimport;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {TestConfiguration.class})
public abstract class TestBase {
}
