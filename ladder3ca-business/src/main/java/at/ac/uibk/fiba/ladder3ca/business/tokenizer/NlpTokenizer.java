package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import java.util.List;

public interface NlpTokenizer {
    List<TokenizedToken> getTokens(TokenizerLanguage language, String inputText) throws Exception;

    String normalize(String text);
}
