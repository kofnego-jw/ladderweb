package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

public class TokenizedToken {

    public final String origForm;
    public final String token;
    public final int start;
    public final int end;

    public TokenizedToken(String origForm, String token, int start, int end) {
        this.origForm = origForm;
        this.token = token;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "TokenizedToken{" +
                "origForm='" + origForm + '\'' +
                ", token='" + token + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
