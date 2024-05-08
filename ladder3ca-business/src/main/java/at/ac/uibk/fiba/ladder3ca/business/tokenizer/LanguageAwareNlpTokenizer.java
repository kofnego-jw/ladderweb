package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import java.util.List;

public class LanguageAwareNlpTokenizer implements NlpTokenizer {

    @Override
    public List<TokenizedToken> getTokens(TokenizerLanguage language, String inputText) throws Exception {
        MyTokenizerFactory tf = new MyTokenizerFactory(language);
        MyDocumentTokenizer tokenizer = tf.create(inputText, false);
        return tokenizer.getTokens();
    }

    @Override
    public String normalize(String text) {
        return text;
    }
}
