package at.ac.uibk.fiba.ficker.docx2tei.service.impl;

import at.ac.uibk.fiba.ficker.docx2tei.Docx2TeiException;
import at.ac.uibk.fiba.ficker.docx2tei.service.Docx2TeiResult;
import at.ac.uibk.fiba.ficker.docx2tei.service.Docx2TeiService;
import net.lingala.zip4j.ZipFile;
import net.sf.saxon.s9api.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Component
public class Docx2TeiServiceImpl implements Docx2TeiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Docx2TeiServiceImpl.class);

    private static final String STYLESHEET_DIR_PREFIX = "ficker_stylesheets-";

    private static final String DOCX_UNZIP_PREFIX = "ficker_docx2tei-";

    private static final String DOCX2TEI_START_STYLESHEET = "docx/from/docxtotei.xsl";

    private static final String DOCX2TEI_START_XML = "word/document.xml";

    private static final String DEFAULT_TEI_FILENAME = "tei.xml";

    private final Processor processor = new Processor(false);

    private File stylesheetDir;

    @Override
    public void close() throws Exception {
        if (stylesheetDir != null) {
            FileUtils.deleteDirectory(stylesheetDir);
        }
    }

    public File getStylesheetFile() throws Docx2TeiException {
        if (stylesheetDir == null || !stylesheetDir.exists()) {
            stylesheetDir = createStylesheetDir();
        }
        File stylesheetFile = new File(stylesheetDir, DOCX2TEI_START_STYLESHEET);
        if (!stylesheetFile.exists()) {
            copyStylesheetResources();
        }
        if (!stylesheetFile.exists()) {
            throw new Docx2TeiException("Cannot find stylesheet file.");
        }
        return stylesheetFile;
    }

    private File getSystemTempDir() {
        return FileUtils.getTempDirectory();
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    private File createStylesheetDir() throws Docx2TeiException {
        String uuid = randomUUID();
        String dirName = STYLESHEET_DIR_PREFIX + uuid;
        File dir = new File(getSystemTempDir(), dirName);
        if (!dir.mkdirs()) {
            LOGGER.error("Cannot create temporary stylesheet directory {}.", dir.getAbsolutePath());
            throw new Docx2TeiException("Cannot create temporary stylesheet directory.");
        }
        return dir;
    }

    private File getDocx2TeiUnzipDir(String uuid) {
        String dirName = DOCX_UNZIP_PREFIX + uuid;
        return new File(getSystemTempDir(), dirName);
    }

    private File createNewDocx2TeiUnzipDir(String uuid) throws Docx2TeiException {
        String dirName = DOCX_UNZIP_PREFIX + uuid;
        File dir = new File(getSystemTempDir(), dirName);
        if (!dir.mkdirs()) {
            LOGGER.error("Cannot create temporary unzip directory {}.", dir.getAbsolutePath());
            throw new Docx2TeiException("Cannot create temporary unzip directory.");
        }
        return dir;
    }

    private File unzipDocxFile(String uuid, File docxFile) throws Docx2TeiException {
        LOGGER.debug("Unzip docx file {} to {}.", docxFile.getAbsolutePath(), uuid);
        File unzipDir = createNewDocx2TeiUnzipDir(uuid);
        try {
            new ZipFile(docxFile).extractAll(unzipDir.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("Cannot unzip docx file.", e);
            throw new Docx2TeiException("Cannot unzip docx file.", e);
        }
        return unzipDir;
    }

    @Override
    public void evict(String uuid) throws Docx2TeiException {
        LOGGER.debug("Evict temporary file with uuid {}.", uuid);
        File dir = getDocx2TeiUnzipDir(uuid);
        if (!dir.exists()) {
            LOGGER.warn("There is no temporary file with uuid {}, cannot evict directory.", uuid);
            return;
        }
        try {
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            LOGGER.error("Cannot delete docx unzip directory.", e);
            throw new Docx2TeiException("Cannot delete docx unzip directory.", e);
        }
    }

    @Override
    public Docx2TeiResult convertToTei(File docxFile) throws Docx2TeiException {
        String uuid = randomUUID();
        File docxDir = unzipDocxFile(uuid, docxFile);
        File stylesheetFile = getStylesheetFile();
        return convertUnzipped(uuid, docxDir, stylesheetFile);
    }

    private Docx2TeiResult convertUnzipped(String uuid, File docxDir, File stylesheetFile) throws Docx2TeiException {
        File startXml = new File(docxDir, DOCX2TEI_START_XML);
        if (!startXml.exists()) {
            throw new Docx2TeiException("Cannot find entry point for docx to tei conversion.");
        }
        File outputFile = new File(docxDir, DEFAULT_TEI_FILENAME);
        try {
            DocumentBuilder documentBuilder = processor.newDocumentBuilder();
            XdmNode xml = documentBuilder.build(new StreamSource(startXml));
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            for (Map.Entry<String, String> entry : Docx2TeiParameter.DEFAULT_PARAMETERS.entrySet()) {
                xsltCompiler.setParameter(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue()));
            }
            xsltCompiler.setParameter(new QName(Docx2TeiParameter.WORD_DIRECTORY_PARAM), new XdmAtomicValue(docxDir.getAbsolutePath()));
            StreamSource xslt = new StreamSource(stylesheetFile);
            XsltTransformer transformer = xsltCompiler.compile(xslt).load();
            Serializer result = processor.newSerializer(outputFile);
            transformer.setInitialContextNode(xml);
            transformer.setDestination(result);
            transformer.transform();
        } catch (Exception e) {
            LOGGER.error("Cannot convert docx to tei.", e);
            throw new Docx2TeiException("Cannot convert docx to tei.", e);
        }
        return new Docx2TeiResult(uuid, outputFile, docxDir);
    }

    private void copyStylesheetResources() throws Docx2TeiException {
        LOGGER.info("Copy stylesheet files to stylesheet directory {}.", stylesheetDir.getAbsolutePath());
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/docx2tei/**");
            for (Resource resource : resources) {
                if (resource.exists() & resource.isReadable() && resource.contentLength() > 0) {
                    URL url = resource.getURL();
                    String urlString = url.toExternalForm();
                    String targetName = urlString.substring(urlString.lastIndexOf("/docx2tei/") + "/docx2tei/".length());
                    File destination = new File(stylesheetDir, targetName);
                    FileUtils.copyURLToFile(url, destination);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot copy classpath resources to stylesheet directory.", e);
            throw new Docx2TeiException("Cannot copy classpath resources to stylesheet directory.", e);
        }
    }


}
