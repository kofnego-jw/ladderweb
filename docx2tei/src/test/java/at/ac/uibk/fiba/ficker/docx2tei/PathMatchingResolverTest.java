package at.ac.uibk.fiba.ficker.docx2tei;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.net.URL;

public class PathMatchingResolverTest {

    private static final File OUTPUT_DIR = new File("./target/test/resolver");

    @Test
    public void test() throws Exception {
        if (OUTPUT_DIR.exists()) {
            FileUtils.deleteDirectory(OUTPUT_DIR);
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/docx2tei/docx/**");
        for (Resource resource : resources) {
            if (resource.exists() & resource.isReadable() && resource.contentLength() > 0) {
                URL url = resource.getURL();
                String urlString = url.toExternalForm();
                String targetName = urlString.substring(urlString.indexOf("/docx2tei/docx/"));
                File destination = new File(OUTPUT_DIR, targetName);
                FileUtils.copyURLToFile(url, destination);
            }
        }
    }
}
