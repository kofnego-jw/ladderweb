package at.ac.uibk.fiba.ladder3ca.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SortClause {

    public final LadderField field;

    public final boolean asc;

    @JsonCreator
    public SortClause(@JsonProperty("field") LadderField field, @JsonProperty("asc") boolean asc) {
        this.field = field;
        this.asc = asc;
    }
}
