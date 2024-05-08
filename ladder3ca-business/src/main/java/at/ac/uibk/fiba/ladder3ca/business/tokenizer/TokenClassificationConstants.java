package at.ac.uibk.fiba.ladder3ca.business.tokenizer;

public class TokenClassificationConstants {

    public static final int WORD2VEC_LAYERCOUNT = 20;
    public static final int MINIMAL_WORD_FREQUENCY = 1;
    public static final int WINDOW_SIZE = 5;
    public static final int ITERATION_COUNT = 1;

    public static final String END_OF_SENTENCE = "[EOS]";

    public static final int LAYER1_N_COUNT = 2 * WORD2VEC_LAYERCOUNT;
    public static final int LAYER2_N_COUNT = 2 * LAYER1_N_COUNT;
    public static final int OUTPUT_N_COUNT = 2; // Binary classifier

}
