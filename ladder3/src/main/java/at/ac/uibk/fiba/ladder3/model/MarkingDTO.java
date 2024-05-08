package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkingDTO {

    public final String markedText;

    public final String type;

    public final String subtype;

    public final int from;

    public final int to;

    @JsonCreator
    public MarkingDTO(
            @JsonProperty("markedText") String markedText,
            @JsonProperty("type") String type,
            @JsonProperty("subtype") String subtype,
            @JsonProperty("from") int from,
            @JsonProperty("to") int to) {
        this.markedText = markedText;
        this.type = type;
        this.subtype = subtype;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "MarkingDTO{" +
                "markedText='" + markedText + '\'' +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
