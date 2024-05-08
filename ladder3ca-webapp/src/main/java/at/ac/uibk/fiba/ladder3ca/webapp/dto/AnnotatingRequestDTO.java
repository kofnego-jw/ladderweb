package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AnnotatingRequestDTO {

    public final String text;
    public final List<Long> modifierIds;
    public final List<Long> subactIds;
    public final String language;
    public final boolean useMax;

    @JsonCreator
    public AnnotatingRequestDTO(
            @JsonProperty("text") String text,
            @JsonProperty("modifierIds") List<Long> modifierIds,
            @JsonProperty("subactIds") List<Long> subactIds,
            @JsonProperty("language") String language,
            @JsonProperty("useMax") boolean useMax) {
        this.text = text;
        this.modifierIds = modifierIds;
        this.subactIds = subactIds;
        this.language = language;
        this.useMax = useMax;
    }
}
