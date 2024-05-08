package at.ac.uibk.fiba.ladder3ca.business.service;

import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.business.model.AnnotatingResult;
import at.ac.uibk.fiba.ladder3ca.business.model.TokenInfo;
import at.ac.uibk.fiba.ladder3ca.business.model.TrainingConstants;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.NlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizedToken;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizerLanguage;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.*;
import opennlp.tools.postag.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class POSModelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(POSModelsService.class);

    private final AnnotatedTextService textService;
    private final NlpTokenizer tokenizer;
    private final File modelsDir;
    private final AuditLogService logService;

    public POSModelsService(AnnotatedTextService textService, NlpTokenizer tokenizer, File modelsDir, AuditLogService logService) {
        this.textService = textService;
        this.tokenizer = tokenizer;
        this.modelsDir = modelsDir;
        this.logService = logService;
    }

    private File getModifierModelFile(Long id, String language) {
        return new File(modelsDir, "modifier-" + id + "_" + language + ".model");
    }

    private File getSubactModelFile(Long id, String language) {
        return new File(modelsDir, "subact-" + id + "_" + language + ".model");
    }

    public POSModel getModifierModel(Long modifierId, String language) throws Exception {
        File modelsFile = getModifierModelFile(modifierId, language);
        if (modelsFile.exists()) {
            return new POSModel(new FileInputStream(modelsFile));
        }
        throw new EntityMissingException("Cannot find POS model file");
    }

    public POSModel getSubactModel(Long subactId, String language) throws Exception {
        File modelsFile = getSubactModelFile(subactId, language);
        if (modelsFile.exists()) {
            return new POSModel(new FileInputStream(modelsFile));
        }
        throw new EntityMissingException("Cannot find POS model file");
    }

    public AnnotatingResult annotateModifier(String originalText, Long modifierId, String language, boolean useMax) throws Exception {
        AnnotationModifier modifier = textService.findModifierById(modifierId);
        TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(language);
        try {
            POSModel model = getModifierModel(modifierId, language);
            String tag = modifier.getModifierCode();
            return annotateWithModel(originalText, useMax, model, tag, tokenizerLanguage, modifier, null);
        } catch (EntityMissingException e) {
            LOGGER.error("Cannot annotate modifier.", e);
            return new AnnotatingResult(originalText, Collections.emptyList());
        }
    }

    public AnnotatingResult annotateSubact(String originalText, Long subactId, String language, boolean useMax) throws Exception {
        AnnotationSubact subact = textService.findSubactById(subactId);
        TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(language);
        try {
            POSModel model = getSubactModel(subactId, language);
            String tag = subact.getSubactName();
            return annotateWithModel(originalText, useMax, model, tag, tokenizerLanguage, null, subact);
        } catch (EntityMissingException e) {
            LOGGER.error("Cannot annotate subact.", e);
            return new AnnotatingResult(originalText, Collections.emptyList());
        }
    }

    private File createTrainFile(AnnotationModifier modifier, String language) throws Exception {
        File txtFile = new File(modelsDir, "train-mod-" + modifier.getId() + "_" + language + ".txt");
        List<AnnotatedText> texts = textService.listAllByLanguage(language);
        TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(language);
        StringBuilder sb = new StringBuilder();
        for (AnnotatedText text : texts) {
            List<ModifierAnnotation> annotations = textService.listByTextAndModifier(text.getId(), modifier.getId());
            List<String> tokens = tokenizer.getTokens(tokenizerLanguage, text.getTextdata())
                    .stream().map(x -> x.token).collect(Collectors.toList());
            for (int i = 0; i < tokens.size(); i++) {
                int index = i;
                boolean tagged = annotations.stream().anyMatch(an -> an.getStartTn() <= index && index <= an.getEndTn());
                sb.append(tokens.get(i)).append("_");
                if (tagged) {
                    sb.append(TrainingConstants.FOUND_TAG);
                } else {
                    sb.append(TrainingConstants.IGNORED_TAG);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        FileUtils.writeStringToFile(txtFile, sb.toString(), StandardCharsets.UTF_8);
        return txtFile;
    }

    private File createTrainFile(AnnotationSubact subact, String language) throws Exception {
        File txtFile = new File(modelsDir, "train-sub-" + subact.getId() + "_" + language + ".txt");
        List<AnnotatedText> texts = textService.listAllByLanguage(language);
        TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(language);
        StringBuilder sb = new StringBuilder();
        for (AnnotatedText text : texts) {
            List<SubactAnnotation> annotations = textService.listByTextAndSubact(text.getId(), subact.getId());
            List<String> tokens = tokenizer.getTokens(tokenizerLanguage, text.getTextdata()).stream()
                    .map(x -> x.token).collect(Collectors.toList());
            for (int i = 0; i < tokens.size(); i++) {
                int index = i;
                boolean tagged = annotations.stream().anyMatch(an -> an.getStartTn() <= index && index <= an.getEndTn());
                sb.append(tokens.get(i)).append("_");
                if (tagged) {
                    sb.append(TrainingConstants.FOUND_TAG);
                } else {
                    sb.append(TrainingConstants.IGNORED_TAG);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        FileUtils.writeStringToFile(txtFile, sb.toString(), StandardCharsets.UTF_8);
        return txtFile;
    }

    public void retrainModifierModel(Long modifierId, String language, AppUser user) throws Exception {
        AnnotationModifier modifier = textService.findModifierById(modifierId);
        File modelFile = getModifierModelFile(modifierId, language);
        trainModel(modifierId, createTrainFile(modifier, language), language, modelFile);
        logService.log("Modifier model trained.", user);
    }

    public void retrainSubactModel(Long subactId, String language, AppUser user) throws Exception {
        AnnotationSubact subact = textService.findSubactById(subactId);
        File modelFile = getSubactModelFile(subactId, language);
        trainModel(subactId, createTrainFile(subact, language), language, modelFile);
        logService.log("Subact model trained.", user);
    }

    private void trainModel(Long modOrActId, File trainFile2, String languageCode, File modelFile) throws Exception {
        POSModel model = null;
        TrainingParameters parameters = TrainingParameters.defaultParams();
        parameters.put(TrainingParameters.ITERATIONS_PARAM, "100");
        try (InputStream dataIn = new FileInputStream(trainFile2)) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(() -> dataIn, StandardCharsets.UTF_8);
            ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
            model = POSTaggerME.train(languageCode, sampleStream, parameters, new POSTaggerFactory());
        }
        try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile))) {
            model.serialize(modelOut);
        }
    }

    private AnnotatingResult annotateWithModel(String originalText, boolean useMax, POSModel model, String tag, TokenizerLanguage language, AnnotationModifier modifier, AnnotationSubact subact) throws Exception {
        List<TokenizedToken> tokens = tokenizer.getTokens(language, originalText);
        POSTaggerME tagger = new POSTaggerME(model);
        Sequence[] sequences = tagger.topKSequences(tokens.stream().map(x -> x.token).toArray(String[]::new));
        if (sequences == null || sequences.length == 0) {
            throw new Exception("No sequence information retrieved.");
        }
        List<TokenInfo> tokenInfoList = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            TokenizedToken token = tokens.get(i);
            TokenInfo info = new TokenInfo(token.origForm, token.token, token.start, token.end, i);
            tokenInfoList.add(info);
            boolean addTag = sequences[0].getOutcome(i).equals(TrainingConstants.FOUND_TAG);
            if (!addTag && useMax) {
                int index = i;
                addTag = Stream.of(sequences).anyMatch(x -> x.getOutcome(index).equals(TrainingConstants.FOUND_TAG));
            }
            if (addTag) {
                if (subact != null) {
                    info.addTag(subact);
                }
                if (modifier != null) {
                    info.addTag(modifier);
                }
            }
        }
        return new AnnotatingResult(originalText, tokenInfoList);
    }
}
