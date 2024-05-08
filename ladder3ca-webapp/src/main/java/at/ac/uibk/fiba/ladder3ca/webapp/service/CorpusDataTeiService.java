package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.model.SearchRequest;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CorpusDataTeiService {

    public static final String LADDER_XML_2_TEI_PATH = "/xslt/ladderxml2tei.xsl";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final Processor processor;
    private final XsltExecutable xml2teiExecutable;

    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;

    public CorpusDataTeiService(XmlMapper xmlMapper, ObjectMapper objectMapper) throws Exception {
        this.processor = new Processor(false);
        this.xmlMapper = xmlMapper;
        this.objectMapper = objectMapper;
        this.xml2teiExecutable = createXsltExecutable();
    }

    private XsltExecutable createXsltExecutable() throws Exception {
        XsltCompiler xsltCompiler = processor.newXsltCompiler();
        StreamSource xsltIs = new StreamSource(getClass().getResourceAsStream(LADDER_XML_2_TEI_PATH));
        return xsltCompiler.compile(xsltIs);
    }


    public byte[] toXML(CorpusData corpusData, SearchRequest request) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xmlMapper.writeValue(baos, corpusData);
        StreamSource xml = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));
        XsltTransformer transformer = xml2teiExecutable.load();
        String currentDate = FORMATTER.format(LocalDateTime.now());
        transformer.setParameter(new QName("currentDate"), new XdmAtomicValue(currentDate));
        if (request == null) {
            transformer.setParameter(new QName("title"), new XdmAtomicValue("All Data from Ladder 3 CA"));
            transformer.setParameter(new QName("query"), new XdmAtomicValue("*:*"));
        } else {
            transformer.setParameter(new QName("title"), new XdmAtomicValue("Selected Data from Ladder 3 CA"));
            String requestString = objectMapper.writeValueAsString(request);
            transformer.setParameter(new QName("query"), new XdmAtomicValue(requestString));
        }
        transformer.setSource(xml);
        baos.reset();
        Serializer dest = processor.newSerializer(baos);
        transformer.setDestination(dest);
        transformer.transform();
        return baos.toByteArray();
    }
}
