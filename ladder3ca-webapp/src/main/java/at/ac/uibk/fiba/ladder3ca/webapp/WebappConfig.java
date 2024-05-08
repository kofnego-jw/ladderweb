package at.ac.uibk.fiba.ladder3ca.webapp;

import at.ac.uibk.fiba.ladder3ca.business.BusinessConfig;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import({BusinessConfig.class})
@EnableAutoConfiguration
@ComponentScan
//@EnableScheduling
public class WebappConfig {

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }
}
