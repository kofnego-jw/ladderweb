package at.ac.uibk.fiba.ficker.docx2tei.service;

import java.io.File;

/**
 * This class models the result of a Docx2Tei conversion.
 * After the conversion, it is good to clean up the temporary directory
 * by calling Docx2TeiService#evict(Docx2TeiResult)
 */
public class Docx2TeiResult {

    /**
     * The UUID of the conversion
     */
    public final String uuid;

    /**
     * The result TEI file
     */
    public final File teiFile;

    /**
     * The directory to which the DOCX file has been unzipped. It can contain images and other embedded media files.
     */
    public final File wordUnzipDir;

    public Docx2TeiResult(String uuid, File teiFile, File wordUnzipDir) {
        this.uuid = uuid;
        this.teiFile = teiFile;
        this.wordUnzipDir = wordUnzipDir;
    }
}