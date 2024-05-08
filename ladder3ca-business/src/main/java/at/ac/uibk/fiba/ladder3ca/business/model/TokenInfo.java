package at.ac.uibk.fiba.ladder3ca.business.model;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenInfo {

    public final String original;
    public final String token;
    public final int start;
    public final int end;
    public final int tokenIndex;
    public final List<Long> modifierIds = new ArrayList<>();
    public final List<Long> subactIds = new ArrayList<>();

    public final List<String> tags = new ArrayList<>();

    @JsonCreator
    public TokenInfo(@JsonProperty("original") String original,
                     @JsonProperty("token") String token,
                     @JsonProperty("start") int start,
                     @JsonProperty("end") int end,
                     @JsonProperty("tokenIndex") int tokenIndex,
                     @JsonProperty("tags") List<String> tags,
                     @JsonProperty("modifierIds") List<Long> modifierIds,
                     @JsonProperty("subactIds") List<Long> subactIds) {
        this.original = original;
        this.token = token;
        this.start = start;
        this.end = end;
        this.tokenIndex = tokenIndex;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (modifierIds != null) {
            this.modifierIds.addAll(modifierIds);
        }
        if (subactIds != null) {
            this.subactIds.addAll(subactIds);
        }
    }

    public TokenInfo(String original, String token, int start, int end, int tokenIndex) {
        this(original, token, start, end, tokenIndex, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public void addTag(AnnotationModifier modifier) {
        if (!this.tags.contains(modifier.getModifierCode())) {
            this.tags.add(modifier.getModifierCode());
        }
        if (!this.modifierIds.contains(modifier.getId())) {
            this.modifierIds.add(modifier.getId());
        }
    }

    public void addTag(AnnotationSubact subact) {
        if (!this.tags.contains(subact.getSubactFullName())) {
            this.tags.add(subact.getSubactFullName());
        }
        if (!this.subactIds.contains(subact.getId())) {
            this.subactIds.add(subact.getId());
        }
    }

    public void addTag(String s) {
        if (!this.tags.contains(s)) {
            this.tags.add(s);
        }
    }

    public void addModifierId(Long id) {
        if (!this.modifierIds.contains(id)) {
            this.modifierIds.add(id);
        }
    }

    public void addSubactId(Long id) {
        if (!this.subactIds.contains(id)) {
            this.subactIds.add(id);
        }
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "token='" + token + '\'' +
                ", tags=" + tags +
                '}';
    }
}
