package at.ac.uibk.fiba.ladder3ca.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AnnotatingResult {

    public final String originalText;

    public final List<TokenInfo> tokenInfos;

    @JsonCreator
    public AnnotatingResult(@JsonProperty("originalText") String originalText,
                            @JsonProperty("tokenInfos") List<TokenInfo> tokenInfos) {
        this.originalText = originalText;
        this.tokenInfos = tokenInfos;
    }

    @Override
    public String toString() {
        return "AnnotatingResult{" +
                "originalText='" + originalText + '\'' +
                ", tokenInfos=" + tokenInfos +
                '}';
    }
}
