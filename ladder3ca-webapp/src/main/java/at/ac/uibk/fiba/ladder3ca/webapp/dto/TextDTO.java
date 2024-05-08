package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TextDTO {
    public final String id;
    public final String altId;
    public final String textdata;
    public final String languageCode;
    public final CreationTaskDTO creationTask;
    public final long lastModified;

    @JsonCreator
    public TextDTO(@JsonProperty("id") String id,
                   @JsonProperty("altId") String altId,
                   @JsonProperty("textdata") String textdata,
                   @JsonProperty("languageCode") String languageCode,
                   @JsonProperty("creationTask") CreationTaskDTO creationTask,
                   @JsonProperty("lastModified") long lastModified) {
        this.id = id;
        this.altId = altId;
        this.textdata = textdata;
        this.languageCode = languageCode;
        this.creationTask = creationTask;
        this.lastModified = lastModified;
    }

    public static TextDTO fromAnnotatedText(AnnotatedText txt) {
        if (txt == null) {
            return null;
        }
        return new TextDTO(txt.getId(), txt.getAltId(), txt.getTextdata(), txt.getLanguageCode(),
                CreationTaskDTO.fromCreationTask(txt.getCreationTask()),
                txt.getLastModified().atOffset(ZoneOffset.UTC).toEpochSecond());
    }

    public static List<TextDTO> fromAnnotatedTextList(List<AnnotatedText> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(TextDTO::fromAnnotatedText).collect(Collectors.toList());
    }
}
