package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ModifierMarking {

    public final Modifier modifier;

    public final Evidence evidence;

    public final String text;

    public final int from;

    public final int to;

    @JsonCreator
    public ModifierMarking(
            @JsonProperty("modifier") Modifier modifier,
            @JsonProperty("evidence") Evidence evidence,
            @JsonProperty("text") String text,
            @JsonProperty("from") int from,
            @JsonProperty("to") int to) {
        this.modifier = modifier;
        this.evidence = evidence;
        this.text = text;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifierMarking that = (ModifierMarking) o;
        return from == that.from && to == that.to && Objects.equals(modifier, that.modifier) && Objects.equals(evidence, that.evidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifier, evidence, from, to);
    }

    @Override
    public String toString() {
        return "ModifierMarking{" +
                "modifier=" + modifier +
                ", evidence=" + evidence +
                ", text='" + text + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
