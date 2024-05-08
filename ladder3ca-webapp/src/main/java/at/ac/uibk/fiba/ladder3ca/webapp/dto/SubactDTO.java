package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubactDTO implements Comparable<SubactDTO> {
    public final Long id;
    public final String subactName;
    public final Long parentSubactId;
    public final SubactDTO parentSubact;
    public final String desc;

    @JsonCreator
    public SubactDTO(@JsonProperty("id") Long id,
                     @JsonProperty("subactName") String subactName,
                     @JsonProperty("parentSubactId") Long parentSubactId,
                     @JsonProperty("parentSubact") SubactDTO parentSubact,
                     @JsonProperty("desc") String desc) {
        this.id = id;
        this.subactName = subactName;
        this.parentSubactId = parentSubactId;
        this.parentSubact = parentSubact;
        this.desc = desc;
    }

    public static SubactDTO fromAnnotationSubact(AnnotationSubact subact) {
        if (subact == null) {
            return null;
        }
        Long parentId = subact.getParentSubact() == null ? null : subact.getParentSubact().getId();
        return new SubactDTO(subact.getId(), subact.getSubactName(), parentId, SubactDTO.fromAnnotationSubact(subact.getParentSubact()), subact.getDesc());
    }

    public static List<SubactDTO> fromAnnotationSubactList(List<AnnotationSubact> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(SubactDTO::fromAnnotationSubact)
                .sorted().collect(Collectors.toList());
    }

    @JsonIgnore
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (this.parentSubact != null) {
            sb.append(this.parentSubact.getFullName()).append("/");
        }
        sb.append(this.subactName);
        return sb.toString();
    }

    @Override
    public int compareTo(SubactDTO o) {
        return this.getFullName().compareToIgnoreCase(o.getFullName());
    }
}
