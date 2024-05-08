package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "annotation_subact")
public class AnnotationSubact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subact_name", nullable = false)
    private String subactName;

    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_subsubact2subact"))
    @ManyToOne(fetch = FetchType.EAGER)
    private AnnotationSubact parentSubact;

    @Column(name = "description", length = 500)
    private String desc;

    public AnnotationSubact() {
    }

    public AnnotationSubact(String subactName, AnnotationSubact parentSubact, String desc) {
        this.subactName = subactName;
        this.parentSubact = parentSubact;
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubactName() {
        return subactName;
    }

    public void setSubactName(String subactName) {
        this.subactName = subactName;
    }

    public String getSubactFullName() {
        StringBuilder sb = new StringBuilder();
        if (getParentSubact() != null) {
            sb.append(getParentSubact().getSubactFullName()).append("/");
        }
        sb.append(getSubactName());
        return sb.toString();
    }

    public AnnotationSubact getParentSubact() {
        return parentSubact;
    }

    public void setParentSubact(AnnotationSubact parentSubact) {
        this.parentSubact = parentSubact;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationSubact that = (AnnotationSubact) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnnotationSubact{" +
                "id=" + id +
                ", subactName='" + subactName + '\'' +
                ", parentSubact=" + parentSubact +
                ", desc='" + desc + '\'' +
                '}';
    }
}
