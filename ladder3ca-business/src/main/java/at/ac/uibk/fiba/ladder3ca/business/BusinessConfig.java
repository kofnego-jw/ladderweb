package at.ac.uibk.fiba.ladder3ca.business;

import at.ac.uibk.fiba.ladder3ca.business.tokenizer.LanguageAwareNlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.NlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.datamodel.DataModelConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;

@SpringBootConfiguration
@ComponentScan
@Import({DataModelConfig.class})
public class BusinessConfig {

    @Bean
    public File workingDir() {
        File workingDir = new File("workingDir");
        if (!workingDir.exists() && !workingDir.mkdirs()) {
            throw new RuntimeException("Cannot create working directory");
        }
        return workingDir;
    }

    @Bean
    public File modelsDir() {
        File modelsDir = new File(workingDir(), "models");
        if (!modelsDir.exists() && !modelsDir.mkdirs()) {
            throw new RuntimeException("Cannot create models directory");
        }
        return modelsDir;
    }

    @Bean
    public File luceneDir() {
        File luceneDir = new File(workingDir(), "lucene");
        if (!luceneDir.exists() && !luceneDir.mkdirs()) {
            throw new RuntimeException("Cannot create lucene directory");
        }
        return luceneDir;
    }


    @Bean
    public NlpTokenizer nlpTokenizer() {
        return new LanguageAwareNlpTokenizer();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
