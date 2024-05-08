package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AnnotationSubactRepository extends JpaRepository<AnnotationSubact, Long> {

    @Query("SELECT subact FROM AnnotationSubact AS subact WHERE subact.subactName = ?1")
    Optional<AnnotationSubact> findBySubactName(String subactName);
}
