package at.ac.uibk.fiba.ladder3ca.datamodel.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "creation_task")
public class CreationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_name", nullable = false, unique = true)
    private String taskName;

    @Column(name = "description", length = 500)
    private String desc;

    @Column(name = "category")
    private String category;

    public CreationTask() {
    }

    public CreationTask(String taskName, String desc, String category) {
        this.taskName = taskName;
        this.desc = desc;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreationTask creationTask = (CreationTask) o;
        return Objects.equals(id, creationTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
