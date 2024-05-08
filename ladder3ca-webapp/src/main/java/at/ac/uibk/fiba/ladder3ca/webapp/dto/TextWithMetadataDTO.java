package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.SpeakerGender;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

public class TextWithMetadataDTO extends TextDTO {

    public final SpeakerGender gender;
    public final Integer ageAtCreation;
    public final String l1Language;
    public final String l2Languages;
    public final String location;
    public final List<String> tokens;

    @JsonCreator
    public TextWithMetadataDTO(@JsonProperty("id") String id,
                               @JsonProperty("altId") String altId,
                               @JsonProperty("textdata") String textdata,
                               @JsonProperty("languageCode") String languageCode,
                               @JsonProperty("creationTask") CreationTaskDTO creationTask,
                               @JsonProperty("lastModified") long lastModified,
                               @JsonProperty("gender") SpeakerGender gender,
                               @JsonProperty("ageAtCreation") Integer ageAtCreation,
                               @JsonProperty("l1Language") String l1Language,
                               @JsonProperty("l2Languages") String l2Languages,
                               @JsonProperty("location") String location,
                               @JsonProperty("tokens") List<String> tokens) {
        super(id, altId, textdata, languageCode, creationTask, lastModified);
        this.gender = gender;
        this.ageAtCreation = ageAtCreation;
        this.l1Language = l1Language;
        this.l2Languages = l2Languages;
        this.location = location;
        this.tokens = tokens == null ? Collections.emptyList() : tokens;
    }

    public static TextWithMetadataDTO fromTextMetadata(TextMetadata metadata, List<String> tokens) {
        if (metadata == null || metadata.getText() == null) {
            return null;
        }
        return new TextWithMetadataDTO(metadata.getId(), metadata.getText().getAltId(), metadata.getText().getTextdata(), metadata.getText().getLanguageCode(),
                CreationTaskDTO.fromCreationTask(metadata.getText().getCreationTask()),
                metadata.getText().getLastModified().atOffset(ZoneOffset.UTC).toEpochSecond(), metadata.getGender(),
                metadata.getAgeAtCreation(), metadata.getL1Language(), metadata.getL2Languages(), metadata.getLocation(), tokens);
    }

    public static TextWithMetadataDTO fromTextWithoutMetadata(AnnotatedText text) {
        if (text == null) {
            return null;
        }
        return new TextWithMetadataDTO(text.getId(), text.getAltId(), text.getTextdata(), text.getLanguageCode(), CreationTaskDTO.fromCreationTask(text.getCreationTask()), text.getLastModified().atOffset(ZoneOffset.UTC).toEpochSecond(),
                null, null, null, null, null, Collections.emptyList());
    }

    @JsonIgnore
    public String getCreationTaskName() {
        if (this.creationTask == null) {
            return "";
        }
        return creationTask.taskName;
    }
}
