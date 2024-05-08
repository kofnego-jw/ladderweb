package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "subact_annotation")
public class SubactAnnotation implements Comparable<SubactAnnotation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "text_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private AnnotatedText annotatedText;

    @JoinColumn(name = "modifier_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private AnnotationSubact subact;

    @Column(name = "start_tn")
    private Long startTn;

    @Column(name = "end_tn")
    private Long endTn;

    public SubactAnnotation() {
    }

    public SubactAnnotation(AnnotatedText annotatedText, AnnotationSubact subact, Long startTn, Long endTn) {
        this.annotatedText = annotatedText;
        this.subact = subact;
        this.startTn = startTn;
        this.endTn = endTn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnnotatedText getAnnotatedText() {
        return annotatedText;
    }

    public void setAnnotatedText(AnnotatedText annotatedText) {
        this.annotatedText = annotatedText;
    }

    public AnnotationSubact getSubact() {
        return subact;
    }

    public void setSubact(AnnotationSubact subact) {
        this.subact = subact;
    }

    public Long getStartTn() {
        return startTn;
    }

    public void setStartTn(Long startTn) {
        this.startTn = startTn;
    }

    public Long getEndTn() {
        return endTn;
    }

    public void setEndTn(Long endTn) {
        this.endTn = endTn;
    }

    @Override
    public int compareTo(SubactAnnotation o) {
        if (o == null) {
            return -1;
        }
        return Long.compare(this.getStartTn(), o.getStartTn());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubactAnnotation that = (SubactAnnotation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SubactAnnotation{" +
                "id=" + id +
                ", annotatedText=" + annotatedText +
                ", subact=" + subact +
                ", startTn=" + startTn +
                ", endTn=" + endTn +
                '}';
    }
}
