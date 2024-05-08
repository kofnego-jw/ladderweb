package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "annotation_modifier")
public class AnnotationModifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "modifier_code", nullable = false)
    private String modifierCode;

    @Column(name = "description", length = 500)
    private String desc;

    public AnnotationModifier() {
    }

    public AnnotationModifier(String modifierCode, String desc) {
        this.modifierCode = modifierCode;
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModifierCode() {
        return modifierCode;
    }

    public void setModifierCode(String modifierCode) {
        this.modifierCode = modifierCode;
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
        AnnotationModifier that = (AnnotationModifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnnotationModifier{" +
                "id=" + id +
                ", modifierCode='" + modifierCode + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
