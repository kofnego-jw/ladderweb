package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.model.AnnotationFW;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.business.service.AnnotationModelService;
import at.ac.uibk.fiba.ladder3ca.business.service.EntityMissingException;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.NlpTokenizer;
import at.ac.uibk.fiba.ladder3ca.business.tokenizer.TokenizerLanguage;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.*;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CrudService {

    private final AnnotatedTextService textService;
    private final AnnotationModelService annotationService;
    private final NlpTokenizer tokenizer;

    public CrudService(AnnotatedTextService textService, AnnotationModelService annotationService, NlpTokenizer tokenizer) {
        this.textService = textService;
        this.annotationService = annotationService;
        this.tokenizer = tokenizer;
    }

    private AppUser getPrincipal() {
        return SecurityUtils.getAppUser();
    }

    // CreationTask
    public List<CreationTaskDTO> listAllCreationTasks() {
        List<CreationTask> list = textService.listAllCreationTasks();
        return CreationTaskDTO.fromCreationTaskList(list);
    }

    public CreationTaskDTO findCreationTaskById(Long id) {
        Optional<CreationTask> opt = textService.findCreationTaskById(id);
        if (opt.isPresent()) {
            return CreationTaskDTO.fromCreationTask(opt.get());
        }
        throw ExceptionHelper.notFound();
    }

    public CreationTaskDTO addNewCreationTask(CreationTaskDTO dto) {
        SecurityUtils.needUserOrAdmin();
        CreationTask task = textService.addNewCreationTask(dto.taskName, dto.desc, dto.category, getPrincipal());
        return CreationTaskDTO.fromCreationTask(task);
    }

    public CreationTaskDTO updateCreationTask(CreationTaskDTO dto) {
        SecurityUtils.needUserOrAdmin();
        CreationTask task = textService.updateCreationTask(dto.id, dto.taskName, dto.desc, dto.category, getPrincipal());
        return CreationTaskDTO.fromCreationTask(task);
    }

    public List<CreationTaskDTO> deleteCreationTask(Long creationTaskId) {
        SecurityUtils.needUserOrAdmin();
        textService.deleteCreationTask(creationTaskId, getPrincipal());
        return listAllCreationTasks();
    }

    // Text
    public List<TextDTO> listAllTexts() {
        List<AnnotatedText> annotatedTexts = textService.listAll();
        return TextDTO.fromAnnotatedTextList(annotatedTexts);
    }

    public TextWithMetadataDTO readOneTextWithMetadata(String textId) {
        try {
            TextMetadata text = textService.findTextMetadataById(textId);
            TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(text.getText().getLanguageCode());
            List<String> tokens = tokenizer.getTokens(tokenizerLanguage, text.getText().getTextdata())
                    .stream().map(x -> x.token).collect(Collectors.toList());
            return TextWithMetadataDTO.fromTextMetadata(text, tokens);
        } catch (EntityMissingException e) {
            try {
                AnnotatedText text = textService.findTextById(textId);
                return TextWithMetadataDTO.fromTextWithoutMetadata(text);
            } catch (EntityMissingException e2) {
                throw ExceptionHelper.notFound();
            } catch (Exception e2) {
                throw ExceptionHelper.internalError("Cannot read text.", e);
            }
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot read text.", e);
        }
    }

    public TextWithMetadataDTO addNewText(TextWithMetadataDTO dto) {
        SecurityUtils.needUserOrAdmin();
        Long creationTaskId = dto.creationTask == null ? null : dto.creationTask.id;
        try {
            TextMetadata metadata = textService.addNewTextWithMetadata(dto.altId, dto.textdata, dto.languageCode, creationTaskId,
                    dto.gender, dto.ageAtCreation, dto.l1Language, dto.l2Languages, dto.location, getPrincipal());
            TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(metadata.getText().getLanguageCode());
            List<String> tokens = tokenizer.getTokens(tokenizerLanguage, metadata.getText().getTextdata())
                    .stream().map(x -> x.token).collect(Collectors.toList());
            return TextWithMetadataDTO.fromTextMetadata(metadata, tokens);
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot add new text.", e);
        }
    }

    public TextWithMetadataDTO updateTextWithMetadata(TextWithMetadataDTO dto) {
        SecurityUtils.needUserOrAdmin();
        Long creationTaskId = dto.creationTask == null ? null : dto.creationTask.id;
        try {
            TextMetadata result = textService.updateTextAndMetadata(dto.id, dto.altId, dto.textdata, dto.languageCode, creationTaskId,
                    dto.gender, dto.ageAtCreation, dto.l1Language, dto.l2Languages, dto.location, getPrincipal());
            TokenizerLanguage tokenizerLanguage = TokenizerLanguage.guessLanguage(result.getText().getLanguageCode());
            List<String> tokens = tokenizer.getTokens(tokenizerLanguage, result.getText().getTextdata())
                    .stream().map(x -> x.token).collect(Collectors.toList());
            return TextWithMetadataDTO.fromTextMetadata(result, tokens);
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot update text.", e);
        }
    }

    public List<TextDTO> deleteTextById(String textId) {
        SecurityUtils.needUserOrAdmin();
        try {
            textService.deleteText(textId, getPrincipal());
            return listAllTexts();
        } catch (EntityMissingException e) {
            return listAllTexts();
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot delete text.", e);
        }
    }

    // Modifier
    public List<ModifierDTO> listAllModifiers() {
        List<AnnotationModifier> list = annotationService.listModifiers();
        return ModifierDTO.fromAnnotationModifierList(list);
    }

    public ModifierDTO readModifier(Long modifierId) {
        AnnotationModifier modifier = annotationService.findModifierByIdOrNull(modifierId);
        if (modifier != null) {
            return ModifierDTO.fromAnnotationModifier(modifier);
        }
        throw ExceptionHelper.notFound();
    }

    public ModifierDTO addNewModifier(ModifierDTO dto) {
        SecurityUtils.needUserOrAdmin();
        try {
            AnnotationModifier modifier = annotationService.createModifier(dto.modifierCode, dto.desc, getPrincipal());
            return ModifierDTO.fromAnnotationModifier(modifier);
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot add modifier.", e);
        }
    }

    public ModifierDTO updateModifier(ModifierDTO dto) {
        SecurityUtils.needUserOrAdmin();
        try {
            AnnotationModifier modifier = annotationService.updateModifier(dto.id, dto.modifierCode, dto.desc, getPrincipal());
            return ModifierDTO.fromAnnotationModifier(modifier);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot update modifier.", e);
        }
    }

    public List<ModifierDTO> deleteModifierById(Long modifierId) {
        SecurityUtils.needUserOrAdmin();
        annotationService.deleteModifier(modifierId, getPrincipal());
        return listAllModifiers();
    }

    // Subact
    public List<SubactDTO> listAllSubacts() {
        List<AnnotationSubact> list = annotationService.listSubacts();
        return SubactDTO.fromAnnotationSubactList(list);
    }

    public SubactDTO readSubact(Long subactId) {
        Optional<AnnotationSubact> opt = annotationService.findSubactById(subactId);
        if (opt.isPresent()) {
            return SubactDTO.fromAnnotationSubact(opt.get());
        }
        throw ExceptionHelper.notFound();
    }

    public SubactDTO addNewSubact(SubactDTO dto) {
        SecurityUtils.needUserOrAdmin();
        try {
            AnnotationSubact subact = annotationService.createOrGetSubact(dto.subactName, dto.parentSubactId, dto.desc, getPrincipal());
            return SubactDTO.fromAnnotationSubact(subact);
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot add subact.", e);
        }
    }

    public SubactDTO updateSubact(SubactDTO dto) {
        SecurityUtils.needUserOrAdmin();
        try {
            AnnotationSubact subact = annotationService.updateSubact(dto.id, dto.subactName, dto.parentSubactId, dto.desc, getPrincipal());
            return SubactDTO.fromAnnotationSubact(subact);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        } catch (Exception e) {
            throw ExceptionHelper.internalError("Cannot update subact.", e);
        }
    }

    public List<SubactDTO> deleteSubactById(Long subactId) {
        SecurityUtils.needUserOrAdmin();
        annotationService.deleteSubact(subactId, getPrincipal());
        return listAllSubacts();
    }

    public List<ModifierAnnotationDTO> getModifierAnnotationsByText(String textId) {
        try {
            List<ModifierAnnotation> list = annotationService.getModifierAnnotationByText(textId);
            return ModifierAnnotationDTO.fromModifierAnnotationList(list);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        }
    }

    public List<ModifierAnnotationDTO> updateModifierAnnotationByText(String textId, List<ModifierAnnotationDTO> dtos) {
        SecurityUtils.needUserOrAdmin();
        try {
            List<AnnotationFW> annotationList = dtos.stream().map(dto -> AnnotationFW.fromModifierAnnotation(dto.modifierId, dto.startTn, dto.endTn)).collect(Collectors.toList());
            List<ModifierAnnotation> list = annotationService.updateModifierAnnotationsByText(textId, annotationList, getPrincipal());
            return ModifierAnnotationDTO.fromModifierAnnotationList(list);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        }
    }

    public List<SubactAnnotationDTO> getSubactAnnotationsByText(String textId) {
        try {
            List<SubactAnnotation> list = annotationService.getSubactAnnotationsByText(textId);
            return SubactAnnotationDTO.fromSubactAnnotatonList(list);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        }
    }

    public List<SubactAnnotationDTO> updateSubactAnnotationByText(String textId, List<SubactAnnotationDTO> dtos) {
        SecurityUtils.needUserOrAdmin();
        try {
            List<AnnotationFW> annotationList = dtos.stream().map(dto -> AnnotationFW.fromSubactAnnotation(dto.subactId, dto.startTn, dto.endTn)).collect(Collectors.toList());
            List<SubactAnnotation> list = annotationService.updateSubactAnnotationsByText(textId, annotationList, getPrincipal());
            return SubactAnnotationDTO.fromSubactAnnotatonList(list);
        } catch (EntityMissingException e) {
            throw ExceptionHelper.notFound();
        }
    }


    public List<TextDTO> listLatestTexts(Integer count) {
        if (count == null || count < 5) {
            count = 5;
        }
        List<AnnotatedText> latest = textService.listLatest(count);
        return TextDTO.fromAnnotatedTextList(latest);
    }
}
