package at.ac.uibk.fiba.ladder3ca.business;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {BusinessConfig.class})
public abstract class TestBase {
}
