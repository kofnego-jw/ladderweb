package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import opennlp.tools.sentdetect.SentenceDetectorME;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDocumentTokenizer {

    private static final Pattern NO_LETTER_BREAK = Pattern.compile("\\p{L}+");
    private final List<TokenizedToken> tokens;
    private final boolean preProcessToken;
    private int currentTokenCount = 0;

    public MyDocumentTokenizer(TokenizerLanguage lang, String text, boolean preProcessToken) {
        this.preProcessToken = preProcessToken;
        this.tokens = processText(lang, text);
    }

    private List<TokenizedToken> processText(TokenizerLanguage lang, String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFC);
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        SentenceDetectorME sentenceDetector = switch (lang) {
            case DE -> NlpSentenceDetector.DE_SENTENCE_DETECTOR;
            case IT, AUTO -> NlpSentenceDetector.IT_SENTENCE_DETECTOR;
        };
        Analyzer analyzer = preProcessToken ? switch (lang) {
            case DE -> NlpAnalyzers.DE_STEMMING_ANALYZER;
            case IT -> NlpAnalyzers.IT_STEMMING_ANALYZER;
            case AUTO -> NlpAnalyzers.NO_STEMMING_ANALYZER;
        } : NlpAnalyzers.NO_STEMMING_ANALYZER;
        try {
            String[] sentences = sentenceDetector.sentDetect(text);
            int sentenceStart = 0;
            List<TokenizedToken> result = new ArrayList<>();
            for (String sentence : sentences) {
                StringReader sr = new StringReader(sentence);
                try (TokenStream tokenStream = analyzer.tokenStream("", sr)) {
                    tokenStream.addAttribute(OffsetAttribute.class);
                    tokenStream.reset();
                    while (tokenStream.incrementToken()) {
                        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
                        OffsetAttribute offset = tokenStream.getAttribute(OffsetAttribute.class);
                        String token = charTerm.toString();
                        String orig = sentence.substring(offset.startOffset(), offset.endOffset());
                        int start = offset.startOffset() + sentenceStart;
                        int end = offset.endOffset() + sentenceStart;
                        result.add(new TokenizedToken(orig, token, start, end));
                    }
                }
                int sentenceEnd = sentenceStart + sentence.length() - 1;
                result.add(new TokenizedToken("", "[EOS]", sentenceEnd, sentenceEnd + 1));
                sentenceStart += sentence.length() + 1;
            }
            return result;
        } catch (Throwable t) {
            // If anything fails, use failsafe
            return failed(text);
        }
    }


    private List<TokenizedToken> failed(String text) {
        List<TokenizedToken> result = new ArrayList<>();
        Matcher matcher = NO_LETTER_BREAK.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String orig = matcher.group();
            String token = orig.toLowerCase();
            result.add(new TokenizedToken(orig, token, start, end));
        }
        return result;
    }

    public boolean hasMoreTokens() {
        return currentTokenCount < tokens.size();
    }

    public int countTokens() {
        return tokens.size();
    }

    public TokenizedToken nextToken() {
        TokenizedToken token = tokens.get(currentTokenCount);
        currentTokenCount++;
        return token;
    }

    public List<TokenizedToken> getTokens() {
        List<TokenizedToken> tokens = new ArrayList<>();
        while (hasMoreTokens()) {
            tokens.add(nextToken());
        }
        return tokens;
    }

}
