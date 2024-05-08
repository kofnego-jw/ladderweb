package at.ac.uibk.fiba.ladder3ca.dataimport.service;

import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.dataimport.TestBase;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DataImportServiceTest extends TestBase {

    private static final File CORPUS_FILE = new File("src/test/resources/ladderdata.json");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataImportService importService;

    @Autowired
    private AnnotatedTextService textService;

    @Test
    public void test() throws Exception {
        int before = textService.listAll().size();
        CorpusData corpusData = objectMapper.readValue(CORPUS_FILE, CorpusData.class);
        Map<String, List<String>> result = importService.importData(corpusData);
        result.entrySet().forEach(entry -> {
            System.out.println(entry.getKey());
            entry.getValue().forEach(m -> System.out.println("  " + m));
        });
        Assertions.assertEquals(corpusData.records.size(), textService.listAll().size() - before);
    }
}
