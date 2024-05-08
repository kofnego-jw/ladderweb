package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Subact {

    public final String name;

    public final Subact parentAct;

    @JsonCreator
    public Subact(@JsonProperty("name") String name, @JsonProperty("parentAct") Subact parentAct) {
        this.name = name;
        this.parentAct = parentAct;
    }

    @JsonIgnore
    public String getFullname() {
        if (this.parentAct != null) {
            return this.parentAct.getFullname() + " / " + this.name;
        }
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subact subact = (Subact) o;
        return Objects.equals(name, subact.name) && Objects.equals(parentAct, subact.parentAct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentAct);
    }
}
