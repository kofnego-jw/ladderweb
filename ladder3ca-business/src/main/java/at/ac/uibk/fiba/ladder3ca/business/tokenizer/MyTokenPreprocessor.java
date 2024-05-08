package at.ac.uibk.fiba.ladder3ca.business.tokenizer;


public class MyTokenPreprocessor {

    private final StemmerWrapper stemmerWrapper;

    public MyTokenPreprocessor(StemmerWrapper stemmerWrapper) {
        this.stemmerWrapper = stemmerWrapper;
    }

    public String preProcess(String token) {
        return stemmerWrapper.filterToken(token);
    }
}
