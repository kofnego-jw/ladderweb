package at.ac.uibk.fiba.ladder3ca.business.service;

import at.ac.uibk.fiba.ladder3ca.business.audit.AuditLogService;
import at.ac.uibk.fiba.ladder3ca.business.model.AnnotationFW;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.*;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnotationModelService {

    private final AnnotationModifierRepository modifierRepository;
    private final AnnotationSubactRepository subactRepository;
    private final ModifierAnnotationRepository modifierAnnotationRepository;
    private final SubactAnnotationRepository subactAnnotationRepository;
    private final AnnotatedTextService textService;
    private final AnnotatedTextRepository textRepository;
    private final AuditLogService logService;

    public AnnotationModelService(AnnotationModifierRepository modifierRepository,
                                  AnnotationSubactRepository subactRepository,
                                  ModifierAnnotationRepository modifierAnnotationRepository,
                                  SubactAnnotationRepository subactAnnotationRepository,
                                  AnnotatedTextService textService,
                                  AnnotatedTextRepository textRepository,
                                  AuditLogService logService) {
        this.modifierRepository = modifierRepository;
        this.subactRepository = subactRepository;
        this.modifierAnnotationRepository = modifierAnnotationRepository;
        this.subactAnnotationRepository = subactAnnotationRepository;
        this.textService = textService;
        this.textRepository = textRepository;
        this.logService = logService;
    }

    public AnnotationModifier createModifier(String modifierName, String desc, AppUser user) throws Exception {
        Optional<AnnotationModifier> inDbOpt = modifierRepository.findByModifierCode(modifierName);
        if (inDbOpt.isPresent()) {
            throw new Exception("Modifier with the same code already exists.");
        }
        AnnotationModifier modifier = new AnnotationModifier(modifierName, desc);
        modifierRepository.save(modifier);
        logService.log("The modifier '" + modifier.getId() + "' created.", user);
        return modifier;
    }

    public Optional<AnnotationSubact> findSubactById(Long id) {
        return subactRepository.findById(id);
    }

    public Optional<AnnotationModifier> findModifierById(Long id) {
        return modifierRepository.findById(id);
    }

    public AnnotationSubact findSubactByIdOrNull(Long id) {
        if (id == null) {
            return null;
        }
        Optional<AnnotationSubact> opt = findSubactById(id);
        return opt.orElse(null);
    }

    public AnnotationModifier findModifierByIdOrNull(Long id) {
        if (id == null) {
            return null;
        }
        return findModifierById(id).orElse(null);
    }

    public AnnotationSubact createOrGetSubact(String subactName, Long parentId, String desc, AppUser user) throws Exception {
        Optional<AnnotationSubact> inDbOpt = subactRepository.findBySubactName(subactName);
        if (inDbOpt.isPresent()) {
            return inDbOpt.get();
        }
        AnnotationSubact par = parentId == null ? null : findSubactByIdOrNull(parentId);
        AnnotationSubact act = new AnnotationSubact(subactName, par, desc);
        subactRepository.save(act);
        logService.log("The subact '" + act.getId() + "' created.", user);
        return act;
    }

    public List<AnnotationModifier> listModifiers() {
        return modifierRepository.findAll();
    }

    public List<AnnotationSubact> listSubacts() {
        return subactRepository.findAll();
    }

    public AnnotationModifier updateModifier(Long modifierId, String modifierCode, String desc, AppUser user) throws Exception {
        Optional<AnnotationModifier> inDbOpt = findModifierById(modifierId);
        if (inDbOpt.isEmpty()) {
            return createModifier(modifierCode, desc, user);
        }
        AnnotationModifier modifier = inDbOpt.get();
        modifier.setModifierCode(modifierCode);
        modifier.setDesc(desc);
        modifierRepository.save(modifier);
        logService.log("The modifier '" + modifierId + "' updated.", user);
        return modifier;
    }

    public AnnotationSubact updateSubact(Long subactId, String subactName, Long parentId, String desc, AppUser user) throws Exception {
        Optional<AnnotationSubact> inDbOpt = findSubactById(subactId);
        if (inDbOpt.isEmpty()) {
            return createOrGetSubact(subactName, parentId, desc, user);
        }
        AnnotationSubact subact = inDbOpt.get();
        AnnotationSubact parent = findSubactByIdOrNull(parentId);
        subact.setSubactName(subactName);
        subact.setParentSubact(parent);
        subact.setDesc(desc);
        subactRepository.save(subact);
        logService.log("The subact '" + subactId + "' updated.", user);
        return subact;
    }

    public void deleteModifier(Long modifierId, AppUser user) {
        if (modifierId != null) {
            AnnotationModifier modifier = findModifierByIdOrNull(modifierId);
            if (modifier != null) {
                modifierRepository.delete(modifier);
                logService.log("The modifier '" + modifierId + "' deleted.", user);
            }
        }
    }

    public void deleteSubact(Long subactId, AppUser user) {
        if (subactId != null) {
            AnnotationSubact subact = findSubactByIdOrNull(subactId);
            if (subact != null) {
                subactRepository.delete(subact);
                logService.log("The subact '" + subactId + "' deleted.", user);
            }
        }
    }

    public List<ModifierAnnotation> getModifierAnnotationByText(String textId) throws EntityMissingException {
        AnnotatedText text = textService.findTextById(textId);
        return modifierAnnotationRepository.findByText(text);
    }

    private void addAnnotations(AnnotatedText text, List<AnnotationFW> list, AppUser user) {
        for (AnnotationFW fw : list) {
            if (fw.modifierId != null) {
                AnnotationModifier modifier = findModifierByIdOrNull(fw.modifierId);
                if (modifier != null) {
                    ModifierAnnotation annotation = new ModifierAnnotation(text, modifier, fw.startTn, fw.endTn);
                    modifierAnnotationRepository.save(annotation);
                }
            }
            if (fw.subactId != null) {
                AnnotationSubact subact = findSubactByIdOrNull(fw.subactId);
                if (subact != null) {
                    SubactAnnotation annotation = new SubactAnnotation(text, subact, fw.startTn, fw.endTn);
                    subactAnnotationRepository.save(annotation);
                }
            }
        }
        logService.log("Annotations for text '" + text.getId() + "' updated.", user);
    }

    public List<ModifierAnnotation> updateModifierAnnotationsByText(String textId, List<AnnotationFW> annotationList, AppUser user) throws EntityMissingException {
        AnnotatedText text = textService.findTextById(textId);
        List<ModifierAnnotation> inDb = getModifierAnnotationByText(textId);
        modifierAnnotationRepository.deleteAll(inDb);
        List<AnnotationFW> toWorkOn = annotationList.stream().filter(x -> x.modifierId != null && x.modifierId > 0).collect(Collectors.toList());
        addAnnotations(text, toWorkOn, user);
        text.setLastModified(LocalDateTime.now());
        textRepository.save(text);
        return modifierAnnotationRepository.findByText(text);
    }

    public List<SubactAnnotation> getSubactAnnotationsByText(String textId) throws EntityMissingException {
        AnnotatedText text = textService.findTextById(textId);
        return subactAnnotationRepository.findByText(text);
    }

    public List<SubactAnnotation> updateSubactAnnotationsByText(String textId, List<AnnotationFW> annotationList, AppUser user) throws EntityMissingException {
        AnnotatedText text = textService.findTextById(textId);
        List<SubactAnnotation> inDb = getSubactAnnotationsByText(textId);
        subactAnnotationRepository.deleteAll(inDb);
        List<AnnotationFW> toWorkOn = annotationList.stream().filter(x -> x.subactId != null && x.subactId > 0).collect(Collectors.toList());
        addAnnotations(text, toWorkOn, user);
        text.setLastModified(LocalDateTime.now());
        textRepository.save(text);
        return subactAnnotationRepository.findByText(text);
    }


}
