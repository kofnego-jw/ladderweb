package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.SubactAnnotation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubactAnnotationDTO {
    public final Long id;
    public final String textId;
    public final Long subactId;
    public final Long startTn;
    public final Long endTn;

    @JsonCreator
    public SubactAnnotationDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("textId") String textId,
            @JsonProperty("subactId") Long subactId,
            @JsonProperty("startTn") Long startTn,
            @JsonProperty("endTn") Long endTn) {
        this.id = id;
        this.textId = textId;
        this.subactId = subactId;
        this.startTn = startTn;
        this.endTn = endTn;
    }

    public static SubactAnnotationDTO fromSubactAnnotation(SubactAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        return new SubactAnnotationDTO(annotation.getId(), annotation.getAnnotatedText().getId(), annotation.getSubact().getId(), annotation.getStartTn(), annotation.getEndTn());
    }

    public static List<SubactAnnotationDTO> fromSubactAnnotatonList(List<SubactAnnotation> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().sorted().map(SubactAnnotationDTO::fromSubactAnnotation).collect(Collectors.toList());
    }
}
