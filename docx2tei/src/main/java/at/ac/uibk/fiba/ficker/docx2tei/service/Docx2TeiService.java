package at.ac.uibk.fiba.ficker.docx2tei.service;

import at.ac.uibk.fiba.ficker.docx2tei.Docx2TeiException;

import java.io.File;

public interface Docx2TeiService extends AutoCloseable {

    /**
     * Method for converting a DOCX file to TEI/XML
     *
     * @param docxFile the docx file in question
     * @return the result of the conversion
     * @throws Docx2TeiException when exception happens
     */
    Docx2TeiResult convertToTei(File docxFile) throws Docx2TeiException;

    /**
     * Removes the result of a conversion from the temporary directory
     *
     * @param uuid the uuid in question
     * @throws Docx2TeiException when the deletion fails
     */
    void evict(String uuid) throws Docx2TeiException;

    /**
     * Removes the result of a conversion from the temporary directory
     *
     * @param docx2TeiResult the result of the conversion
     * @throws Docx2TeiException when deletion fails
     */
    default void evict(Docx2TeiResult docx2TeiResult) throws Docx2TeiException {
        if (docx2TeiResult == null) {
            return;
        }
        evict(docx2TeiResult.uuid);
    }
}
