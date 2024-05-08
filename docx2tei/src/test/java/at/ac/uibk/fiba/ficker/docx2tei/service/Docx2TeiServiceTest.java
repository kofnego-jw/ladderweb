package at.ac.uibk.fiba.ficker.docx2tei.service;

import at.ac.uibk.fiba.ficker.docx2tei.TestBase;
import at.ac.uibk.fiba.ficker.docx2tei.service.impl.Docx2TeiServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class Docx2TeiServiceTest extends TestBase {

    public static final File SAMPLE_FILE = new File("src/test/resources/sample/Basil, Otto_31 Br_21 OB an LF_10 LF an OB.docx");
    public static final File TEST_OUTPUT_FILE = new File("src/test/resources/sample/tei.xml");

    @Autowired
    private Docx2TeiService service;

    @Test
    public void conversion() throws Exception {
        if (TEST_OUTPUT_FILE.exists()) {
            FileUtils.forceDelete(TEST_OUTPUT_FILE);
        }
        Assertions.assertNotNull(service);
        Docx2TeiResult result = service.convertToTei(SAMPLE_FILE);
        Assertions.assertTrue(result.teiFile.exists());
        FileUtils.copyFile(result.teiFile, TEST_OUTPUT_FILE);
        Assertions.assertTrue(TEST_OUTPUT_FILE.exists());
        service.evict(result);
        Assertions.assertFalse(result.wordUnzipDir.exists());
        Assertions.assertTrue(TEST_OUTPUT_FILE.exists());
    }

    @Test
    public void autoRecreateStylesheets() throws Exception {
        Docx2TeiResult result = service.convertToTei(SAMPLE_FILE);
        Docx2TeiServiceImpl impl = (Docx2TeiServiceImpl) service;
        impl.evict(result);
        File stylesheetFile = impl.getStylesheetFile();
        Assertions.assertTrue(stylesheetFile.exists());
        File styleParent = stylesheetFile.getParentFile().getParentFile().getParentFile();
        System.out.println(styleParent.getName());
        FileUtils.deleteDirectory(styleParent);
        Docx2TeiResult docx2TeiResult2 = service.convertToTei(SAMPLE_FILE);
        File stylesheetFile2 = impl.getStylesheetFile();
        System.out.println(stylesheetFile2.getParentFile().getParentFile().getParentFile().getName());
        Assertions.assertTrue(stylesheetFile2.exists());
        Assertions.assertFalse(stylesheetFile.exists());
        service.evict(docx2TeiResult2);
    }


}
