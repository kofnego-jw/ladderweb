package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AnnotationModifierRepository extends JpaRepository<AnnotationModifier, Long> {

    @Query("SELECT modifier FROM AnnotationModifier AS modifier WHERE modifier.modifierCode = ?1")
    Optional<AnnotationModifier> findByModifierCode(String modifierName);
}
