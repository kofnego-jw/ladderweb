package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Pool {

    public final String name;

    public final String createdOn;

    public final String modifiedOn;

    @JsonCreator
    public Pool(
            @JsonProperty("name") String name,
            @JsonProperty("createdOn") String createdOn,
            @JsonProperty("modifiedOn") String modifiedOn) {
        this.name = name;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pool pool = (Pool) o;
        return Objects.equals(name, pool.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Pool{" +
                "name='" + name + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", modifiedOn='" + modifiedOn + '\'' +
                '}';
    }
}
