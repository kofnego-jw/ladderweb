package at.ac.uibk.fiba.ladder3ca.business.service;

import at.ac.uibk.fiba.ladder3.model.*;
import at.ac.uibk.fiba.ladder3.repository.Ladder3RepositoryService;
import at.ac.uibk.fiba.ladder3ca.business.TestBase;
import at.ac.uibk.fiba.ladder3ca.business.model.AnnotatingResult;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.NlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizedToken;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizerLanguage;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.ModifierAnnotation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class POSModelsServiceTest extends TestBase {
    public static final File MATERIAL_DIR = new File("../../ladder3/materials");
    public static final File DATA_DIR = new File(MATERIAL_DIR, "nlp/data");

    public static final Pattern ANNOTATION_PATTERN = Pattern.compile("^(.*)_([^_]+)$");

    @Autowired
    private AnnotatedTextService textService;

    @Autowired
    private POSModelsService posModelsService;

    @Autowired
    private AnnotationModelService annotationModelService;

    @Autowired
    private NlpTokenizer nlpTokenizer;


    private void saveModifier(Modifier m) {
        AnnotationModifier am = new AnnotationModifier(m.modifierName, "Desc of " + m.modifierName);
        try {
            annotationModelService.createModifier(m.modifierName, "Desc of " + m.modifierName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSubact(Subact s) {
        if (s.parentAct != null) {
            saveSubact(s.parentAct);
        }
        boolean inDb = annotationModelService.listSubacts().stream().anyMatch(x -> x.getSubactName().equals(s.name));
        if (!inDb) {
            Long parentId = s.parentAct == null ? null :
                    annotationModelService.listSubacts().stream().filter(x -> x.getSubactName().equals(s.parentAct.name)).findAny().get().getId();
            try {
                annotationModelService.createOrGetSubact(s.name, parentId, "Desc of " + s.name, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveText(Evidence x) {
        String langCode = x.evidenceName.startsWith("AUS") ? "de" : "it";
        try {
            textService.addNewTextWithMetadata(x.evidenceName, x.originalAssertions,
                    langCode, null, null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveModifierMarking(ModifierMarking marking) {
        String altId = marking.evidence.evidenceName;
        AnnotatedText annotatedText = textService.listAll().stream().filter(y -> y.getAltId().equals(altId)).findAny().get();
        TokenizerLanguage language = annotatedText.getLanguageCode().equals("de") ? TokenizerLanguage.DE : TokenizerLanguage.IT;
        String name = marking.modifier.modifierName;
        AnnotationModifier modifier = annotationModelService.listModifiers().stream().filter(x -> x.getModifierCode().equals(name)).findAny().orElse(null);
        Long modifierId = modifier.getId();
        try {
            List<TokenizedToken> tokens = nlpTokenizer.getTokens(language, annotatedText.getTextdata());
            for (int i = 0; i < tokens.size(); i++) {
                TokenizedToken t = tokens.get(i);
                if (marking.from <= t.start && t.start < marking.to) {
                    textService.addNewModifierAnnotation(annotatedText.getId(), modifierId, (long) i, (long) i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSubactMarking(SubactMarking marking) {
        String altId = marking.evidence.evidenceName;
        AnnotatedText annotatedText = textService.listAll().stream().filter(y -> y.getAltId().equals(altId)).findAny().get();
        TokenizerLanguage language = annotatedText.getLanguageCode().equals("de") ? TokenizerLanguage.DE : TokenizerLanguage.IT;
        String name = marking.subact.name;
        AnnotationSubact subact = annotationModelService.listSubacts().stream().filter(x -> x.getSubactName().equals(name))
                .findAny().orElse(null);
        Long subactId = subact.getId();
        try {
            List<TokenizedToken> tokens = nlpTokenizer.getTokens(language, annotatedText.getTextdata());
            for (int i = 0; i < tokens.size(); i++) {
                TokenizedToken t = tokens.get(i);
                if (marking.from <= t.start && t.start < marking.to) {
                    textService.addNewSubactAnnotation(annotatedText.getId(), subactId, (long) i, (long) i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test() throws Exception {
        Ladder3RepositoryService service = new Ladder3RepositoryService(new File("src/test/resources/repo.json"));
        Ladder3Repository repo = service.readFromFile();

        List<Modifier> modifiers = repo.modifiers;
        modifiers.forEach(m -> saveModifier(m));

        List<Subact> subacts = repo.subacts;
        subacts.forEach(s -> saveSubact(s));

        repo.evidences.forEach(x -> saveText(x));

        repo.modifierMarkings.forEach(x -> saveModifierMarking(x));
        repo.subactMarkings.forEach(x -> saveSubactMarking(x));

        List<AnnotatedText> annotatedTexts = textService.listAll();
        List<AnnotationModifier> modifierList = annotationModelService.listModifiers();
        annotatedTexts.forEach(t -> {
            modifierList.forEach(m -> {
                try {
                    List<ModifierAnnotation> annotations = textService.listByTextAndModifier(t.getId(), m.getId());
                    if (!annotations.isEmpty()) {
                        annotations.forEach(a -> {
                            System.out.print("  " + a.getStartTn());
                        });
                        List<TokenizedToken> tokens = nlpTokenizer.getTokens(TokenizerLanguage.guessLanguage(t.getLanguageCode()), t.getTextdata());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        for (AnnotationModifier modifier : modifierList) {
            posModelsService.retrainModifierModel(modifier.getId(), "de", null);
            posModelsService.retrainModifierModel(modifier.getId(), "it", null);
            AnnotatingResult result = posModelsService.annotateModifier("Das ist sehr blöd-", modifier.getId(), "de", true);
            System.out.println(result);
        }

        List<AnnotationSubact> subactList = annotationModelService.listSubacts();
        for (AnnotationSubact sa : subactList) {
            posModelsService.retrainSubactModel(sa.getId(), "de", null);
            posModelsService.retrainSubactModel(sa.getId(), "it", null);

        }


    }


}
