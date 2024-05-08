package at.ac.uibk.fiba.ladder3ca.webapp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {WebappConfig.class})
public abstract class TestBase {
}
