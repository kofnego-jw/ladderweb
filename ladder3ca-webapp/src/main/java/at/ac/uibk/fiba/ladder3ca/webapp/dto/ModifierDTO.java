package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModifierDTO implements Comparable<ModifierDTO> {

    public final Long id;
    public final String modifierCode;
    public final String desc;

    @JsonCreator
    public ModifierDTO(@JsonProperty("id") Long id, @JsonProperty("modifierCode") String modifierCode, @JsonProperty("desc") String desc) {
        this.id = id;
        this.modifierCode = modifierCode;
        this.desc = desc;
    }

    public static ModifierDTO fromAnnotationModifier(AnnotationModifier modifier) {
        if (modifier == null) {
            return null;
        }
        return new ModifierDTO(modifier.getId(), modifier.getModifierCode(), modifier.getDesc());
    }

    public static List<ModifierDTO> fromAnnotationModifierList(List<AnnotationModifier> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(ModifierDTO::fromAnnotationModifier)
                .sorted().collect(Collectors.toList());
    }

    @Override
    public int compareTo(ModifierDTO o) {
        return this.modifierCode.compareToIgnoreCase(o.modifierCode);
    }
}
