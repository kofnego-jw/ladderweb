package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "annotated_text")
public class AnnotatedText {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "alt_id")
    private String altId;

    @Lob
    @Column(name = "textdata", nullable = false)
    private String textdata;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private CreationTask creationTask;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    public AnnotatedText() {
        this.id = UUID.randomUUID().toString();
        this.lastModified = LocalDateTime.now();
    }

    public AnnotatedText(String altId, String textdata, String languageCode) {
        this();
        this.altId = altId;
        this.textdata = textdata;
        this.languageCode = languageCode;
    }

    public AnnotatedText(String altId, String textdata, String languageCode, CreationTask creationTask) {
        this(altId, textdata, languageCode);
        this.creationTask = creationTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    public String getTextdata() {
        return textdata;
    }

    public void setTextdata(String textdata) {
        this.textdata = textdata;
    }

    public CreationTask getCreationTask() {
        return creationTask;
    }

    public void setCreationTask(CreationTask creationTask) {
        this.creationTask = creationTask;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedText that = (AnnotatedText) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnnotatedText{" +
                "id='" + id + '\'' +
                ", altId='" + altId + '\'' +
                ", xml='" + textdata + '\'' +
                ", creationTask=" + creationTask +
                '}';
    }

    public Long getCreationTaskId() {
        return getCreationTask() == null ? null : getCreationTask().getId();
    }

    public Long getLastModifiedNumber() {
        if (getLastModified() == null) {
            return null;
        }
        return getLastModified().atOffset(ZoneOffset.UTC).toEpochSecond();
    }
}
