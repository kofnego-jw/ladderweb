package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3.model.*;
import at.ac.uibk.fiba.ladder3.repository.Ladder3RepositoryService;
import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotationModelService;
import at.ac.uibk.fiba.ladder3ca.business.service.POSModelsService;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.NlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizedToken;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizerLanguage;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.CreationTask;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.CreationTaskDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class InitService {

    private final AnnotatedTextService textService;
    private final AnnotationModelService annotationModelService;
    private final POSModelsService posModelsService;
    private final NlpTokenizer nlpTokenizer;
    private final AuditLogService logService;

    public InitService(AnnotatedTextService textService, AnnotationModelService annotationModelService,
                       POSModelsService posModelsService, NlpTokenizer nlpTokenizer, AuditLogService logService) {
        this.textService = textService;
        this.annotationModelService = annotationModelService;
        this.posModelsService = posModelsService;
        this.nlpTokenizer = nlpTokenizer;
        this.logService = logService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveIfNotExistsModifier(Modifier m) throws Exception {
        boolean alreadyInDb = annotationModelService.listModifiers().stream()
                .anyMatch(inDb -> inDb.getModifierCode().equals(m.modifierName));
        if (!alreadyInDb) {
            AnnotationModifier modifier = annotationModelService.createModifier(m.modifierName, "Desc of " + m.modifierName, null);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AnnotationSubact saveIfNotExistSubact(Subact s) throws Exception {
        Long parentId = null;
        if (s.parentAct != null) {
            parentId = saveIfNotExistSubact(s.parentAct).getId();
        }
        Optional<AnnotationSubact> inDb = annotationModelService.listSubacts().stream().filter(x -> x.getSubactName().equals(s.name)).findAny();
        if (!inDb.isPresent()) {
            AnnotationSubact orGetSubact = annotationModelService.createOrGetSubact(s.name, parentId, "Desc of " + s.getFullname(), null);
            return orGetSubact;
        }
        return inDb.get();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveText(Evidence x, CreationTask ct) throws Exception {
        String langCode = x.evidenceName.startsWith("AUS") ? "de" : "it";
        String l1 = x.evidenceName.toLowerCase().startsWith("l1") ? "it" :
                x.evidenceName.toLowerCase().startsWith("l2") ? "de" :
                        "";
        String l2 = l1.equals("it") ? "de" : l1.equals("de") ? "it" : "";
        String location = "";
        textService.addNewTextWithMetadata(x.evidenceName, x.originalAssertions,
                langCode, ct.getId(), null, null, l1, l2, location, null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveModifierMarking(ModifierMarking marking) throws Exception {
        String altId = marking.evidence.evidenceName;
        AnnotatedText annotatedText = textService.listAll().stream().filter(y -> y.getAltId().equals(altId)).findAny().get();
        TokenizerLanguage language = annotatedText.getLanguageCode().equals("de") ? TokenizerLanguage.DE : TokenizerLanguage.IT;
        String name = marking.modifier.modifierName;
        AnnotationModifier modifier = annotationModelService.listModifiers().stream()
                .filter(x -> x.getModifierCode().toLowerCase().equals(name.toLowerCase())).findAny().orElse(null);
        Long modifierId = modifier.getId();
        List<TokenizedToken> tokens = nlpTokenizer.getTokens(language, annotatedText.getTextdata());
        for (int i = 0; i < tokens.size(); i++) {
            TokenizedToken t = tokens.get(i);
            if (marking.from <= t.start && t.start < marking.to) {
                textService.addNewModifierAnnotation(annotatedText.getId(), modifierId, (long) i, (long) i);
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSubactMarking(SubactMarking marking) throws Exception {
        String altId = marking.evidence.evidenceName;
        AnnotatedText annotatedText = textService.listAll().stream().filter(y -> y.getAltId().equals(altId)).findAny().get();
        TokenizerLanguage language = annotatedText.getLanguageCode().equals("de") ? TokenizerLanguage.DE : TokenizerLanguage.IT;
        AnnotationSubact subact = annotationModelService.listSubacts()
                .stream().filter(x -> x.getSubactName().toLowerCase().equals(marking.subact.name.toLowerCase()))
                .findAny().orElse(null);
        if (subact == null) {
            throw new Exception("Cannot find subact with name: " + marking.subact.name);
        }
        Long subactId = subact.getId();
        List<TokenizedToken> tokens = nlpTokenizer.getTokens(language, annotatedText.getTextdata());
        for (int i = 0; i < tokens.size(); i++) {
            TokenizedToken t = tokens.get(i);
            if (marking.from <= t.start && t.start < marking.to) {
                textService.addNewSubactAnnotation(annotatedText.getId(), subactId, (long) i, (long) i);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CreationTask getOrCreateCreationTask(Long id, String taskName, String category, String desc) {
        if (id != null) {
            Optional<CreationTask> byId = textService.findCreationTaskById(id);
            if (byId.isPresent() && byId.get().getTaskName().equals(taskName)) {
                return byId.get();
            }
        }
        Optional<CreationTask> byTaskName = textService.findCreationTaskByTaskName(taskName);
        if (byTaskName.isPresent()) {
            return byTaskName.get();
        }
        return textService.addNewCreationTask(taskName, category, desc, null);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addData(File repoFile, CreationTaskDTO creationTask) throws Exception {
        Ladder3RepositoryService service = new Ladder3RepositoryService(repoFile);
        Ladder3Repository repo = service.readFromFile();

        List<Modifier> modifiers = repo.modifiers;
        for (Modifier m : modifiers) {
            saveIfNotExistsModifier(m);
        }
        List<Subact> subacts = repo.subacts;
        for (Subact s : subacts) {
            saveIfNotExistSubact(s);
        }

        CreationTask ct = getOrCreateCreationTask(creationTask.id, creationTask.taskName, creationTask.category, creationTask.desc);
        for (Evidence x : repo.evidences) {
            saveText(x, ct);
        }

        for (ModifierMarking x : repo.modifierMarkings) {
            saveModifierMarking(x);
        }
        for (SubactMarking x : repo.subactMarkings) {
            saveSubactMarking(x);
        }
    }

}
