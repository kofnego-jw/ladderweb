package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import org.apache.lucene.analysis.de.GermanLightStemmer;

import java.util.Locale;

public class MyGermanLightStemmer implements StemmerWrapper {

    private final GermanLightStemmer stemmer = new GermanLightStemmer();


    @Override
    public String filterToken(String token) {
        char[] charBuff = token.toLowerCase(Locale.GERMAN).toCharArray();
        int length = stemmer.stem(charBuff, charBuff.length);
        return new String(charBuff, 0, length);
    }

}
