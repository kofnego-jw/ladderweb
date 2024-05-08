package at.ac.uibk.fiba.ladder3ca.datamodel.entity;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "text_metadata")
public class TextMetadata {

    @Id
    @Column(name = "text_id")
    private String id;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn(name = "text_id")
    private AnnotatedText text;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private SpeakerGender gender;

    @Column(name = "age_at_creation")
    private Integer ageAtCreation;

    @Column(name = "l1_language")
    private String l1Language;

    @Column(name = "l2_languages")
    private String l2Languages;

    @Column(name = "location")
    private String location;

    public TextMetadata() {
    }

    public TextMetadata(AnnotatedText text, SpeakerGender gender, Integer ageAtCreation, String l1Language, String l2Languages, String location) {
        this.id = text.getId();
        this.text = text;
        this.gender = gender;
        this.ageAtCreation = ageAtCreation;
        this.l1Language = l1Language;
        this.l2Languages = l2Languages;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public AnnotatedText getText() {
        return text;
    }

    public SpeakerGender getGender() {
        return gender;
    }

    public void setGender(SpeakerGender gender) {
        this.gender = gender;
    }

    public Integer getAgeAtCreation() {
        return ageAtCreation;
    }

    public void setAgeAtCreation(Integer ageAtCreation) {
        this.ageAtCreation = ageAtCreation;
    }

    public String getL1Language() {
        return l1Language;
    }

    public void setL1Language(String l1Language) {
        this.l1Language = l1Language;
    }

    public String getL2Languages() {
        return l2Languages;
    }

    public void setL2Languages(String l2Languages) {
        this.l2Languages = l2Languages;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMetadata metadata = (TextMetadata) o;
        return Objects.equals(text, metadata.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "text=" + text +
                ", gender='" + gender + '\'' +
                ", ageAtCreation=" + ageAtCreation +
                ", l1Language='" + l1Language + '\'' +
                ", l2Languages='" + l2Languages + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
