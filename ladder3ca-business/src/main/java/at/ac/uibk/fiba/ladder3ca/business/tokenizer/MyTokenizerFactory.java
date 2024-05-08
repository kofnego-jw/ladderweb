package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MyTokenizerFactory {

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String LANG_MODEL_RESOURCEPATH = "/sentencemodels/lang.bin";
    private final TokenizerLanguage language;
    private final LanguageDetectorME languageDetector;

    public MyTokenizerFactory(TokenizerLanguage language) throws Exception {
        this.language = language;
        if (this.language == TokenizerLanguage.AUTO) {
            try (InputStream is = getClass().getResourceAsStream(LANG_MODEL_RESOURCEPATH)) {
                LanguageDetectorModel model = new LanguageDetectorModel(is);
                languageDetector = new LanguageDetectorME(model);
            }
        } else {
            languageDetector = null;
        }
    }

    public MyDocumentTokenizer create(String toTokenize, boolean preProcess) {
        return switch (language) {
            case DE, IT -> new MyDocumentTokenizer(language, toTokenize, preProcess);
            case AUTO -> detectLangaugeFirst(toTokenize, preProcess);
        };
    }

    private MyDocumentTokenizer detectLangaugeFirst(String s, boolean preProcess) {
        if (this.languageDetector == null) {
            return new MyDocumentTokenizer(TokenizerLanguage.IT, s, preProcess);
        }
        Language detectedLang = languageDetector.predictLanguage(s);
        if (detectedLang.getLang().toLowerCase().startsWith("de")) {
            return new MyDocumentTokenizer(TokenizerLanguage.DE, s, preProcess);
        }
        return new MyDocumentTokenizer(TokenizerLanguage.IT, s, preProcess);
    }

    public MyDocumentTokenizer create(InputStream toTokenize, boolean preProcess) {
        String test;
        try {
            test = IOUtils.toString(toTokenize, UTF_8);
        } catch (Exception e) {
            test = "";
        }
        return create(test, preProcess);
    }

}
