package at.ac.uibk.fiba.ladder3ca.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchRequest {

    public final List<SearchClause> clauses;

    @JsonCreator
    public SearchRequest(@JsonProperty("clauses") List<SearchClause> clauses) {
        this.clauses = clauses;
    }
}
