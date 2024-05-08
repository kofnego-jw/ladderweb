package at.ac.uibk.fiba.ladder3ca.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchClause {

    public final ClauseMode mode;
    public final LadderField field;
    public final String queryString;

    @JsonCreator
    public SearchClause(@JsonProperty("mode") ClauseMode mode,
                        @JsonProperty("field") LadderField field,
                        @JsonProperty("queryString") String queryString) {
        this.mode = mode;
        this.field = field;
        this.queryString = queryString;
    }
}
