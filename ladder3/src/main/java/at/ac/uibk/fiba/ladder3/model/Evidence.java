package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Evidence {

    public final Pool pool;

    public final String evidenceName;

    public final String originalAssertions;

    @JsonCreator
    public Evidence(@JsonProperty("pool") Pool pool,
                    @JsonProperty("evidenceName") String evidenceName,
                    @JsonProperty("originalAssertions") String originalAssertions) {
        this.pool = pool;
        this.evidenceName = evidenceName;
        this.originalAssertions = originalAssertions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(pool, evidence.pool) && Objects.equals(evidenceName, evidence.evidenceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pool, evidenceName);
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "pool=" + pool +
                ", evidenceName='" + evidenceName + '\'' +
                ", originalAssertions='" + originalAssertions + '\'' +
                '}';
    }
}
