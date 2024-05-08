package at.ac.uibk.fiba.ficker.docx2tei.service.impl;

import java.util.HashMap;
import java.util.Map;

public class Docx2TeiParameter {

    public static final Map<String, String> DEFAULT_PARAMETERS = new HashMap<>();
    public static final String WORD_DIRECTORY_PARAM = "word-directory";

    static {
        DEFAULT_PARAMETERS.put("preserveSpace", "true");
    }
}
