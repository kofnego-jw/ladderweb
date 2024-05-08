package at.ac.uibk.fiba.ladder3ca.business.index;

import at.ac.uibk.fiba.ladder3ca.business.model.LadderField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import java.util.HashMap;
import java.util.Map;

public class LadderAnalyzer {

    private static final PerFieldAnalyzerWrapper INSTANCE = createAnalyzer();

    private static Analyzer getAnalyzer(LadderField field) {
        return switch (field.fieldType) {
            case TEXT -> new GermanAnalyzer();
            case KEYWORD, BOOLEAN, NUMBER -> new KeywordAnalyzer();
        };
    }

    private static PerFieldAnalyzerWrapper createAnalyzer() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        for (LadderField field : LadderField.values()) {
            analyzerMap.put(field.fieldname, getAnalyzer(field));
        }
        return new PerFieldAnalyzerWrapper(new GermanAnalyzer(), analyzerMap);
    }

    public static Analyzer getAnalyzer() {
        return INSTANCE;
    }
}
