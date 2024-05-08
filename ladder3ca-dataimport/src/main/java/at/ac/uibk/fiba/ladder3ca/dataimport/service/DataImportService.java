package at.ac.uibk.fiba.ladder3ca.dataimport.service;

import at.ac.uibk.fiba.ladder3ca.business.service.AnnotatedTextService;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.CreationTask;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.AnnotationModifierRepository;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.AnnotationSubactRepository;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.*;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.CorpusData;
import at.ac.uibk.fiba.ladder3ca.webapp.serialize.LadderRecord;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataImportService {

    private final AnnotationModifierRepository modifierRepository;
    private final AnnotationSubactRepository subactRepository;
    private final AnnotatedTextService textService;

    public DataImportService(AnnotationModifierRepository modifierRepository, AnnotationSubactRepository subactRepository, AnnotatedTextService textService) {
        this.modifierRepository = modifierRepository;
        this.subactRepository = subactRepository;
        this.textService = textService;
    }

    public Map<String, List<String>> importData(CorpusData data) {
        List<ModifierDTO> modifierList = data.modifierList;
        Map<Long, AnnotationModifier> modifierMap = new HashMap<>();
        modifierList.forEach(m -> modifierMap.put(m.id, findOrCreateModifier(m)));
        List<SubactDTO> subactList = data.subactList;
        Map<Long, AnnotationSubact> subactMap = new HashMap<>();
        subactList.forEach(s -> subactMap.put(s.id, findOrCreateSubact(s)));

        Map<String, List<String>> result = new HashMap<>();
        data.records.forEach(r -> {
            List<String> msg = createIfNotInDb(r, modifierMap, subactMap);
            if (!msg.isEmpty()) {
                result.put(r.textMetadata.id, msg);
            }
        });
        return result;
    }

    private List<String> createIfNotInDb(LadderRecord dto, Map<Long, AnnotationModifier> modifierMap, Map<Long, AnnotationSubact> subactMap) {
        List<String> msg = new ArrayList<>();
        String id = dto.textMetadata.id;
        try {
            textService.findTextMetadataById(id);
            // Text with this id already in db, do not import.
            msg.add("Already in DB, skipped.");
            return msg;
        } catch (Exception e) {
        }
        CreationTask ct = findOrCreateCreationTask(dto.textMetadata.creationTask);
        TextMetadata metadata;
        try {
            metadata = textService.addNewTextWithMetadataAndId(id, dto.textMetadata.altId, dto.textMetadata.textdata,
                    dto.textMetadata.languageCode, ct.getId(), dto.textMetadata.gender, dto.textMetadata.ageAtCreation,
                    dto.textMetadata.l1Language, dto.textMetadata.l2Languages, dto.textMetadata.location, null);
        } catch (Exception e) {
            msg.add(e.getMessage());
            return msg;
        }
        List<ModifierAnnotationDTO> modifierAnnotations = dto.modifierAnnotations;
        for (ModifierAnnotationDTO m : modifierAnnotations) {
            AnnotationModifier modifier = modifierMap.get(m.modifierId);
            try {
                textService.addNewModifierAnnotation(metadata.getId(), modifier.getId(), m.startTn, m.endTn);
            } catch (Exception e) {
                msg.add(e.getMessage());
            }
        }
        List<SubactAnnotationDTO> subactAnnotations = dto.subactAnnotations;
        for (SubactAnnotationDTO s : subactAnnotations) {
            AnnotationSubact subact = subactMap.get(s.subactId);
            try {
                textService.addNewSubactAnnotation(metadata.getId(), subact.getId(), s.startTn, s.endTn);
            } catch (Exception e) {
                msg.add(e.getMessage());
            }
        }
        return msg;
    }

    private CreationTask findOrCreateCreationTask(CreationTaskDTO dto) {
        if (dto.id != null) {
            Optional<CreationTask> byId = textService.findCreationTaskById(dto.id);
            if (byId.isPresent() && byId.get().getTaskName().equals(dto.taskName)) {
                return byId.get();
            }
        }
        Optional<CreationTask> byTaskName = textService.findCreationTaskByTaskName(dto.taskName);
        if (byTaskName.isPresent()) {
            return byTaskName.get();
        }
        return textService.addNewCreationTask(dto.taskName, dto.desc, dto.category, null);
    }


    private AnnotationModifier findOrCreateModifier(ModifierDTO dto) {
        if (dto.id != null) {
            Optional<AnnotationModifier> byId = modifierRepository.findById(dto.id);
            if (byId.isPresent() && byId.get().getModifierCode().equals(dto.modifierCode)) {
                return byId.get();
            }
        }
        Optional<AnnotationModifier> byModifierCode = modifierRepository.findByModifierCode(dto.modifierCode);
        if (byModifierCode.isPresent()) {
            return byModifierCode.get();
        }
        AnnotationModifier newModifier = new AnnotationModifier(dto.modifierCode, dto.desc);
        return modifierRepository.save(newModifier);
    }

    private AnnotationSubact findOrCreateSubact(SubactDTO dto) {
        if (dto.id != null) {
            Optional<AnnotationSubact> byId = subactRepository.findById(dto.id);
            if (byId.isPresent() && byId.get().getSubactName().equals(dto.subactName)) {
                return byId.get();
            }
        }
        Optional<AnnotationSubact> bySubactName = subactRepository.findBySubactName(dto.subactName);
        if (bySubactName.isPresent()) {
            return bySubactName.get();
        }
        AnnotationSubact parent = dto.parentSubact != null ? findOrCreateSubact(dto.parentSubact) : null;
        AnnotationSubact newSubact = new AnnotationSubact(dto.subactName, parent, dto.desc);
        return subactRepository.save(newSubact);
    }

}
