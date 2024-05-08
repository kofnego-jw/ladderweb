package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import org.apache.lucene.analysis.it.ItalianLightStemmer;

import java.util.Locale;

public class MyItalianLightStemmer implements StemmerWrapper {

    private final ItalianLightStemmer stemmer = new ItalianLightStemmer();


    @Override
    public String filterToken(String token) {
        char[] charBuff = token.toLowerCase(Locale.ITALIAN).toCharArray();
        int length = stemmer.stem(charBuff, charBuff.length);
        return new String(charBuff, 0, length);
    }

}
