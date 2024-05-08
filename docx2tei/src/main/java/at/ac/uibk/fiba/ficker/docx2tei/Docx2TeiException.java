package at.ac.uibk.fiba.ficker.docx2tei;

public class Docx2TeiException extends Exception {
    public Docx2TeiException() {
    }

    public Docx2TeiException(String message) {
        super(message);
    }

    public Docx2TeiException(String message, Throwable cause) {
        super(message, cause);
    }

    public Docx2TeiException(Throwable cause) {
        super(cause);
    }
}
