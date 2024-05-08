package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TokenizerTest {

    @Test
    public void test() throws Exception {
        String content = FileUtils.readFileToString(new File("../../ladder3/materials/nlp/testdata/AUS.txt"), StandardCharsets.UTF_8);
        String[] docs = content.split("\\r?\\n");
        MyTokenizerFactory tf = new MyTokenizerFactory(TokenizerLanguage.DE);
        for (String doc : docs) {
            List<TokenizedToken> tokens = tf.create(doc, true).getTokens();
            System.out.println(doc);
            tokens.forEach(System.out::println);
            System.out.println();
        }
    }

}
