package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Modifier {

    public final String modifierName;

    @JsonCreator
    public Modifier(@JsonProperty("modifierName") String modifierName) {
        this.modifierName = modifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifier modifier = (Modifier) o;
        return Objects.equals(modifierName, modifier.modifierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifierName);
    }

    @Override
    public String toString() {
        return "Modifier{" +
                "modifierName='" + modifierName + '\'' +
                '}';
    }
}
