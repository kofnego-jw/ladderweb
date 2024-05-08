package at.ac.uibk.fiba.ladder3ca.webapp.serialize;

import at.ac.uibk.fiba.ladder3ca.webapp.dto.ModifierDTO;
import at.ac.uibk.fiba.ladder3ca.webapp.dto.SubactDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CorpusData {

    public final List<ModifierDTO> modifierList;
    public final List<SubactDTO> subactList;
    public final List<LadderRecord> records;

    @JsonCreator
    public CorpusData(
            @JsonProperty("modifierList") List<ModifierDTO> modifierList,
            @JsonProperty("subactList") List<SubactDTO> subactList,
            @JsonProperty("records") List<LadderRecord> records) {
        this.modifierList = modifierList;
        this.subactList = subactList;
        this.records = records;
    }
}
