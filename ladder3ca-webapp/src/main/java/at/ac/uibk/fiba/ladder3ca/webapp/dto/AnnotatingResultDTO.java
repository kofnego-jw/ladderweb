package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.business.model.AnnotatingResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnnotatingResultDTO {

    public final AnnotatingRequestDTO request;
    public final AnnotatingResult result;

    @JsonCreator
    public AnnotatingResultDTO(
            @JsonProperty("request") AnnotatingRequestDTO request,
            @JsonProperty("result") AnnotatingResult result) {
        this.request = request;
        this.result = result;
    }
}
