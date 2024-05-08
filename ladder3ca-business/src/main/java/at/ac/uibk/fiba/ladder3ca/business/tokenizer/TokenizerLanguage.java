package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import org.apache.commons.lang3.StringUtils;

public enum TokenizerLanguage {
    DE,
    IT,
    AUTO;

    public static TokenizerLanguage guessLanguage(String s) {
        if (StringUtils.isBlank(s)) {
            return AUTO;
        }
        if (s.toLowerCase().startsWith("de")) {
            return DE;
        }
        return IT;
    }
}
