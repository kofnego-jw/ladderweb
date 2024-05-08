package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.CreationTask;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreationTaskDTO {
    public final Long id;
    public final String taskName;
    public final String desc;
    public final String category;

    @JsonCreator
    public CreationTaskDTO(@JsonProperty("id") Long id,
                           @JsonProperty("taskName") String taskName,
                           @JsonProperty("desc") String desc,
                           @JsonProperty("category") String category) {
        this.id = id;
        this.taskName = taskName;
        this.desc = desc;
        this.category = category;
    }

    public static CreationTaskDTO fromCreationTask(CreationTask ct) {
        if (ct == null) {
            return null;
        }
        return new CreationTaskDTO(ct.getId(), ct.getTaskName(), ct.getDesc(), ct.getCategory());
    }

    public static List<CreationTaskDTO> fromCreationTaskList(List<CreationTask> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(CreationTaskDTO::fromCreationTask).collect(Collectors.toList());
    }
}
