package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class TeiDataDTO {

    public final String id;

    public final String text;

    @JacksonXmlElementWrapper(useWrapping = false)
    public final List<MarkingDTO> markedPlaces;

    @JsonCreator
    public TeiDataDTO(
            @JsonProperty("id") String id,
            @JsonProperty("text") String text,
            @JsonProperty("markedPlaces") List<MarkingDTO> markedPlaces) {
        this.id = id;
        this.text = text;
        this.markedPlaces = markedPlaces;
    }

    @Override
    public String toString() {
        return "TeiDataDTO{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", markedPlaces=" + markedPlaces +
                '}';
    }
}
