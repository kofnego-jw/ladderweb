package at.ac.uibk.fiba.ladder3ca.business.service;

import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.*;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnnotatedTextService {

    private final AnnotatedTextRepository textRepository;
    private final TextMetadataRepository metadataRepository;
    private final ModifierAnnotationRepository modifierAnnotationRepository;
    private final SubactAnnotationRepository subactAnnotationRepository;
    private final CreationTaskRepository creationTaskRepository;
    private final AnnotationModifierRepository modifierRepository;
    private final AnnotationSubactRepository subactRepository;
    private final AuditLogService logService;

    public AnnotatedTextService(AnnotatedTextRepository textRepository, TextMetadataRepository metadataRepository, ModifierAnnotationRepository modifierAnnotationRepository, SubactAnnotationRepository subactAnnotationRepository, CreationTaskRepository creationTaskRepository, AnnotationModifierRepository modifierRepository, AnnotationSubactRepository subactRepository, AuditLogService logService) {
        this.textRepository = textRepository;
        this.metadataRepository = metadataRepository;
        this.modifierAnnotationRepository = modifierAnnotationRepository;
        this.subactAnnotationRepository = subactAnnotationRepository;
        this.creationTaskRepository = creationTaskRepository;
        this.modifierRepository = modifierRepository;
        this.subactRepository = subactRepository;
        this.logService = logService;
    }

    public List<AnnotatedText> listAll() {
        return textRepository.findAll();
    }

    public List<ModifierAnnotation> listByTextAndModifier(String textId, Long modifierId) throws Exception {
        AnnotatedText text = findTextById(textId);
        AnnotationModifier modifier = findModifierById(modifierId);
        return modifierAnnotationRepository.listByTextAndModifier(text, modifier);
    }

    public List<SubactAnnotation> listByTextAndSubact(String textId, Long subactId) throws Exception {
        AnnotatedText text = findTextById(textId);
        AnnotationSubact subact = findSubactById(subactId);
        return subactAnnotationRepository.findByTextAndSubact(text, subact);
    }

    public List<SubactAnnotation> listSubactAnnotationsByText(String textId) throws Exception {
        AnnotatedText text = findTextById(textId);
        return subactAnnotationRepository.findByText(text);
    }

    public List<ModifierAnnotation> listModifierAnnotationsByText(String textId) throws Exception {
        AnnotatedText text = findTextById(textId);
        return modifierAnnotationRepository.findByText(text);
    }


    private CreationTask saveInternal(CreationTask task, AppUser user) throws Exception {
        if (task == null) {
            throw new NullPointerException("Cannot save null creation task.");
        }
        task.setId(null);
        CreationTask saved = creationTaskRepository.save(task);
        logService.log("A new creation task created.", user);
        return saved;
    }

    public CreationTask getOrSave(CreationTask task, AppUser user) throws Exception {
        if (task == null) {
            return null;
        }
        if (task.getId() == null) {
            return saveInternal(task, user);
        }
        Optional<CreationTask> inDbOpt = creationTaskRepository.findById(task.getId());
        return inDbOpt.orElse(saveInternal(task, user));
    }

    private AnnotatedText addNewText(String altId, String textdata, String languageCode, CreationTask creationTask, AppUser user) throws Exception {
        AnnotatedText text = new AnnotatedText(altId, textdata, languageCode, creationTask);
        AnnotatedText saved = textRepository.save(text);
        logService.log("A new text '" + saved.getId() + "' added.", user);
        return saved;
    }

    private AnnotatedText addNewTextWithPredifinedId(String id, String altId, String textdata, String languageCode, CreationTask creationTask, AppUser user) throws Exception {
        AnnotatedText text = new AnnotatedText(altId, textdata, languageCode, creationTask);
        text.setId(id);
        AnnotatedText saved = textRepository.save(text);
        logService.log("A new text '" + saved.getId() + "' added.", user);
        return saved;
    }

    public void deleteText(String textId, AppUser user) throws Exception {
        TextMetadata metadata = findTextMetadataById(textId);
        AnnotatedText text = metadata.getText();
        List<ModifierAnnotation> modifierAnnotationsByText = modifierAnnotationRepository.findByText(text);
        modifierAnnotationRepository.deleteAll(modifierAnnotationsByText);
        List<SubactAnnotation> subactAnnotationsByText = subactAnnotationRepository.findByText(text);
        subactAnnotationRepository.deleteAll(subactAnnotationsByText);
        metadataRepository.delete(metadata);
        textRepository.delete(text);
        logService.log("The text '" + text.getId() + "' and its metadata deleted.", user);
    }


    public void deleteModifierAnnotation(Long modifierAnnotationId, AppUser user) {
        if (modifierAnnotationId != null) {
            modifierAnnotationRepository.deleteById(modifierAnnotationId);
            logService.log("The modifier annotation '" + modifierAnnotationId + "' deleted.", user);
        }
    }

    public void deleteSubactAnnotation(Long subactAnnotationId, AppUser user) {
        if (subactAnnotationId != null) {
            subactAnnotationRepository.deleteById(subactAnnotationId);
            logService.log("The subact annotation '" + subactAnnotationId + "' deleted.", user);
        }
    }

    public AnnotatedText findTextById(String id) throws EntityMissingException {
        Optional<AnnotatedText> textOpt = textRepository.findById(id);
        return textOpt.orElseThrow(EntityMissingException::new);
    }

    public TextMetadata findTextMetadataById(String id) throws Exception {
        Optional<TextMetadata> metadataOpt = metadataRepository.findById(id);
        return metadataOpt.orElseThrow(EntityMissingException::new);
    }


    public AnnotationModifier findModifierById(Long id) throws Exception {
        Optional<AnnotationModifier> modifierOpt = modifierRepository.findById(id);
        return modifierOpt.orElseThrow(EntityMissingException::new);
    }

    public AnnotationSubact findSubactById(Long id) throws Exception {
        Optional<AnnotationSubact> subactOpt = subactRepository.findById(id);
        return subactOpt.orElseThrow(EntityMissingException::new);
    }

    public ModifierAnnotation findModifierAnnotationById(Long id) throws Exception {
        Optional<ModifierAnnotation> annotationOpt = modifierAnnotationRepository.findById(id);
        return annotationOpt.orElseThrow(EntityMissingException::new);
    }

    public SubactAnnotation findSubactAnnotationById(Long id) throws Exception {
        Optional<SubactAnnotation> annotationOpt = subactAnnotationRepository.findById(id);
        return annotationOpt.orElseThrow(EntityMissingException::new);
    }

    private boolean annotationOverlapping(Long startTn, Long endTn, Long inDbStart, Long inDbEnd) {
        return startTn >= inDbEnd && endTn <= inDbStart;
    }

    public ModifierAnnotation addNewModifierAnnotation(String textId, Long modifierId, Long startTn, Long endTn) throws Exception {
        AnnotatedText text = findTextById(textId);
        AnnotationModifier modifier = findModifierById(modifierId);
        List<ModifierAnnotation> inDbs = modifierAnnotationRepository.listByTextAndModifier(text, modifier);
        boolean overlap = inDbs.stream().anyMatch(x -> annotationOverlapping(startTn, endTn, x.getStartTn(), x.getEndTn()));
        if (overlap) {
            System.out.println("Overlapping modifier annotation: " + textId + " modifier:" + modifierId + " start:" + startTn + " end:" + endTn);
            //throw new Exception("Overlapping annotation! Please check.");
        }
        ModifierAnnotation annotation = new ModifierAnnotation(text, modifier, startTn, endTn);
        return modifierAnnotationRepository.save(annotation);
    }

    public SubactAnnotation addNewSubactAnnotation(String textId, Long subactId, Long startTn, Long endTn) throws Exception {
        AnnotatedText text = findTextById(textId);
        AnnotationSubact subact = findSubactById(subactId);
        List<SubactAnnotation> inDbs = subactAnnotationRepository.findByTextAndSubact(text, subact);
        boolean overlap = inDbs.stream().anyMatch(x -> annotationOverlapping(startTn, endTn, x.getStartTn(), x.getEndTn()));
        if (overlap) {
            System.out.println("Overlapping subact annotation: " + textId + " subact:" + subactId + " start:" + startTn + " end:" + endTn);
        }
        SubactAnnotation annotation = new SubactAnnotation(text, subact, startTn, endTn);
        return subactAnnotationRepository.save(annotation);
    }

    public ModifierAnnotation updateModifierAnnotation(Long annotationId, Long startTn, Long endTn) throws Exception {
        ModifierAnnotation annotation = findModifierAnnotationById(annotationId);
        List<ModifierAnnotation> inDbs = modifierAnnotationRepository.listByTextAndModifier(annotation.getAnnotatedText(), annotation.getModifier())
                .stream().filter(x -> !x.equals(annotation)).collect(Collectors.toList());
        boolean overlap = inDbs.stream().anyMatch(x -> annotationOverlapping(startTn, endTn, x.getStartTn(), x.getEndTn()));
        if (overlap) {
            throw new Exception("Overlapping annotation! Please check.");
        }
        annotation.setStartTn(startTn);
        annotation.setEndTn(endTn);
        return modifierAnnotationRepository.save(annotation);
    }

    public SubactAnnotation updateSubactAnnotation(Long annotationId, Long startTn, Long endTn) throws Exception {
        SubactAnnotation annotation = findSubactAnnotationById(annotationId);
        List<SubactAnnotation> inDbs = subactAnnotationRepository.findByTextAndSubact(annotation.getAnnotatedText(), annotation.getSubact())
                .stream().filter(x -> !x.equals(annotation)).collect(Collectors.toList());
        boolean overlap = inDbs.stream().anyMatch(x -> annotationOverlapping(startTn, endTn, x.getStartTn(), x.getEndTn()));
        if (overlap) {
            throw new Exception("Overlapping annotation! Please check.");
        }
        annotation.setStartTn(startTn);
        annotation.setEndTn(endTn);
        return subactAnnotationRepository.save(annotation);
    }

    public List<AnnotatedText> listAllByLanguage(String language) {
        return textRepository.findAllByLanguage(language);
    }

    public Optional<CreationTask> findCreationTaskById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return creationTaskRepository.findById(id);
    }

    public Optional<CreationTask> findCreationTaskByTaskName(String taskName) {
        if (StringUtils.isBlank(taskName)) {
            return Optional.empty();
        }
        return creationTaskRepository.findByTaskName(taskName);
    }

    private CreationTask findCreationTaskByIdOrNull(Long id) {
        Optional<CreationTask> findById = findCreationTaskById(id);
        return findById.orElse(null);
    }

    public TextMetadata addNewTextWithMetadata(String altId, String textdata, String languageCode, Long creationTaskId,
                                               SpeakerGender gender, Integer ageAtCreation, String l1Lang, String l2Lang, String location,
                                               AppUser user) throws Exception {
        CreationTask creationTask = findCreationTaskByIdOrNull(creationTaskId);
        AnnotatedText text = addNewText(altId, textdata, languageCode, creationTask, user);
        Integer age = ageAtCreation == null ? null : ageAtCreation <= 0 ? null : ageAtCreation;
        if (text.getId() != null) {
            TextMetadata metadata = new TextMetadata(text, gender, age, l1Lang, l2Lang, location);
            return metadataRepository.save(metadata);
        }
        throw new Exception("Cannot create new text.");
    }

    public TextMetadata addNewTextWithMetadataAndId(String id, String altId, String textdata, String languageCode, Long creationTaskId,
                                                    SpeakerGender gender, Integer ageAtCreation, String l1Lang, String l2Lang, String location,
                                                    AppUser user) throws Exception {
        CreationTask creationTask = findCreationTaskByIdOrNull(creationTaskId);
        AnnotatedText text = id == null ? addNewText(altId, textdata, languageCode, creationTask, user) : addNewTextWithPredifinedId(id, altId, textdata, languageCode, creationTask, user);
        if (text.getId() != null) {
            TextMetadata metadata = new TextMetadata(text, gender, ageAtCreation, l1Lang, l2Lang, location);
            return metadataRepository.save(metadata);
        }
        throw new Exception("Cannot create new text.");
    }

    public TextMetadata updateTextAndMetadata(String textId, String altId, String textdata, String languageCode, Long creationTaskId,
                                              SpeakerGender gender, Integer ageAtCreation, String l1Lang, String l2Lang, String location,
                                              AppUser user) throws Exception {
        TextMetadata inDb = findTextMetadataById(textId);
        if (inDb == null) {
            return addNewTextWithMetadata(altId, textdata, languageCode, creationTaskId, gender, ageAtCreation, l1Lang, l2Lang, location, user);
        }
        AnnotatedText text = inDb.getText();
        Integer age = ageAtCreation == null || ageAtCreation <= 0 ? null : ageAtCreation;
        text.setAltId(altId);
        text.setTextdata(textdata);
        text.setLanguageCode(languageCode);
        CreationTask creationTask = findCreationTaskByIdOrNull(creationTaskId);
        text.setCreationTask(creationTask);
        text.setLastModified(LocalDateTime.now());
        textRepository.save(text);
        inDb.setGender(gender);
        inDb.setAgeAtCreation(age);
        inDb.setL1Language(l1Lang);
        inDb.setL2Languages(l2Lang);
        inDb.setLocation(location);
        TextMetadata saved = metadataRepository.save(inDb);
        logService.log("Text and metadata updated.", user);
        return saved;
    }

    public List<CreationTask> listAllCreationTasks() {
        return creationTaskRepository.findAll();
    }

    public CreationTask addNewCreationTask(String name, String desc, String category, AppUser user) {
        CreationTask task = new CreationTask(name, desc, category);
        creationTaskRepository.save(task);
        logService.log("A new creation task '" + task.getId() + "' created,", user);
        return task;
    }

    public CreationTask updateCreationTask(Long taskId, String name, String desc, String category, AppUser user) {
        Optional<CreationTask> taskOpt = findCreationTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return addNewCreationTask(name, desc, category, user);
        }
        CreationTask task = taskOpt.get();
        task.setTaskName(name);
        task.setDesc(desc);
        task.setCategory(category);
        CreationTask updated = creationTaskRepository.save(task);
        logService.log("The creation task '" + updated.getId() + "' updated.", user);
        return updated;
    }

    public void deleteCreationTask(Long creationTaskId, AppUser user) {
        Optional<CreationTask> taskOpt = findCreationTaskById(creationTaskId);
        if (taskOpt.isPresent()) {
            CreationTask task = taskOpt.get();
            creationTaskRepository.delete(task);
            logService.log("The creation task '" + creationTaskId + "' deleted.", user);
        }

    }

    public List<AnnotatedText> listLatest(int count) {
        PageRequest pr = PageRequest.of(0, count);
        return textRepository.findNewest(pr);
    }

    public List<AnnotatedText> listSince(LocalDateTime lastIndexed) {
        return textRepository.findModifiedSince(lastIndexed);
    }
}
