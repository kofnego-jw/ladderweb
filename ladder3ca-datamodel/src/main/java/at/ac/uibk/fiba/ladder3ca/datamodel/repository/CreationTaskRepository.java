package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.CreationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CreationTaskRepository extends JpaRepository<CreationTask, Long> {

    @Query("SELECT creationTask FROM CreationTask AS creationTask WHERE creationTask.taskName = ?1")
    Optional<CreationTask> findByTaskName(String taskName);
}
