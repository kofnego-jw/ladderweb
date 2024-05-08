package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.ModifierAnnotation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModifierAnnotationDTO {

    public final Long id;
    public final String textId;
    public final Long modifierId;
    public final Long startTn;
    public final Long endTn;

    @JsonCreator
    public ModifierAnnotationDTO(@JsonProperty("id") Long id,
                                 @JsonProperty("textId") String textId,
                                 @JsonProperty("modifierId") Long modifierId,
                                 @JsonProperty("startTn") Long startTn,
                                 @JsonProperty("endTn") Long endTn) {
        this.id = id;
        this.textId = textId;
        this.modifierId = modifierId;
        this.startTn = startTn;
        this.endTn = endTn;
    }

    public static ModifierAnnotationDTO fromModifierAnnotation(ModifierAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        return new ModifierAnnotationDTO(annotation.getId(), annotation.getAnnotatedText().getId(), annotation.getModifier().getId(), annotation.getStartTn(), annotation.getEndTn());
    }

    public static List<ModifierAnnotationDTO> fromModifierAnnotationList(List<ModifierAnnotation> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().sorted().map(ModifierAnnotationDTO::fromModifierAnnotation).collect(Collectors.toList());
    }
}
