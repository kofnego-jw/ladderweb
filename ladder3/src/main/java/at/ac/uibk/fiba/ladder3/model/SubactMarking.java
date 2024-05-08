package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SubactMarking {

    public final Subact subact;

    public final Evidence evidence;

    public final String text;

    public final int from;

    public final int to;

    @JsonCreator
    public SubactMarking(
            @JsonProperty("subact") Subact subact,
            @JsonProperty("evidence") Evidence evidence,
            @JsonProperty("text") String text,
            @JsonProperty("from") int from,
            @JsonProperty("to") int to) {
        this.subact = subact;
        this.evidence = evidence;
        this.text = text;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubactMarking that = (SubactMarking) o;
        return from == that.from && to == that.to && Objects.equals(subact, that.subact) && Objects.equals(evidence, that.evidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subact, evidence, from, to);
    }

    @Override
    public String toString() {
        return "SubactMarking{" +
                "subact=" + subact +
                ", evidence=" + evidence +
                ", text='" + text + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
