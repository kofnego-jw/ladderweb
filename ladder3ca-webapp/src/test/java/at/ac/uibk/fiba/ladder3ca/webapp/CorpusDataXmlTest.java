package at.ac.uibk.fiba.ladder3ca.webapp;

import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class CorpusDataXmlTest {
    private static final File JSON_CORPUS = new File("./src/test/resources/testdata/ladderdata.json");
    private static final File XML_MAPPED = new File("./src/test/resources/testdata/xmlmapper.xml");

    @Test
    public void test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        XmlMapper xm = new XmlMapper();
        CorpusData data = om.readValue(JSON_CORPUS, CorpusData.class);
        Assertions.assertNotNull(data);
        xm.writeValue(XML_MAPPED, data);
    }

}
