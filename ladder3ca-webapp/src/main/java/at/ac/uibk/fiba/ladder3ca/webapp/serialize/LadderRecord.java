package at.ac.uibk.fiba.ladder3ca.webapp.serialize;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.ModifierAnnotationDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.SubactAnnotationDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.TextWithMetadataDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LadderRecord {

    public final TextWithMetadataDTO textMetadata;
    public final List<ModifierAnnotationDTO> modifierAnnotations;
    public final List<SubactAnnotationDTO> subactAnnotations;

    @JsonCreator
    public LadderRecord(
            @JsonProperty("textMetadata") TextWithMetadataDTO textMetadata,
            @JsonProperty("modifierAnnotations") List<ModifierAnnotationDTO> modifierAnnotations,
            @JsonProperty("subactAnnotations") List<SubactAnnotationDTO> subactAnnotations) {
        this.textMetadata = textMetadata;
        this.modifierAnnotations = modifierAnnotations;
        this.subactAnnotations = subactAnnotations;
    }
}
