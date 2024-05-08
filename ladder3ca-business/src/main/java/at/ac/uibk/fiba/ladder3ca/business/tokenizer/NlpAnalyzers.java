package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.de.GermanMinimalStemFilterFactory;
import org.apache.lucene.analysis.it.ItalianLightStemFilterFactory;
import org.apache.lucene.analysis.pattern.PatternTokenizerFactory;

import java.io.IOException;

public class NlpAnalyzers {

    public static final Analyzer NO_STEMMING_ANALYZER;
    public static final Analyzer DE_STEMMING_ANALYZER;
    public static final Analyzer IT_STEMMING_ANALYZER;

    static {
        try {
            NO_STEMMING_ANALYZER = CustomAnalyzer.builder()
                    .withTokenizer(PatternTokenizerFactory.class,
                            "pattern", "(([-\\p{L}\\p{N}]+)|([\\p{P}]{2,}))", "group", "1")
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .build();

            DE_STEMMING_ANALYZER = CustomAnalyzer.builder()
                    .withTokenizer(PatternTokenizerFactory.class,
                            "pattern", "(([-\\p{L}\\p{N}]+)|([\\p{P}]{2,}))", "group", "1")
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .addTokenFilter(GermanMinimalStemFilterFactory.class)
                    .build();
            IT_STEMMING_ANALYZER = CustomAnalyzer.builder()
                    .withTokenizer(PatternTokenizerFactory.class,
                            "pattern", "(([-\\p{L}\\p{N}]+)|([\\p{P}]{2,}))", "group", "1")
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .addTokenFilter(ItalianLightStemFilterFactory.class)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
