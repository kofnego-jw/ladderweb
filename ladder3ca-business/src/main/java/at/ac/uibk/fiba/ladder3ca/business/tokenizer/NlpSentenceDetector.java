package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.util.Objects;

public class NlpSentenceDetector {

    public static final SentenceDetectorME DE_SENTENCE_DETECTOR;
    public static final SentenceDetectorME IT_SENTENCE_DETECTOR;
    private static final String DE_SENTENCE_RESOURCEPATH = "/sentencemodels/de.bin";
    private static final String IT_SENTENCE_RESOURCEPATH = "/sentencemodels/it.bin";

    static {
        try {
            SentenceModel deSentenceModel = new SentenceModel(Objects.requireNonNull(NlpSentenceDetector.class.getResourceAsStream(DE_SENTENCE_RESOURCEPATH)));
            DE_SENTENCE_DETECTOR = new SentenceDetectorME(deSentenceModel);
            SentenceModel itSentenceModel = new SentenceModel(Objects.requireNonNull(NlpSentenceDetector.class.getResourceAsStream(IT_SENTENCE_RESOURCEPATH)));
            IT_SENTENCE_DETECTOR = new SentenceDetectorME(itSentenceModel);
        } catch (Throwable t) {
            // If anything fails, use failsafe
            throw new RuntimeException("Cannot create sentence detectors.", t);
        }
    }

}
