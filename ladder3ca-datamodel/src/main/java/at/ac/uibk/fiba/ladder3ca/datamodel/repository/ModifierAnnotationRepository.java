package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationModifier;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.ModifierAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModifierAnnotationRepository extends JpaRepository<ModifierAnnotation, Long> {

    @Query("SELECT modifierAnnotation FROM ModifierAnnotation AS modifierAnnotation WHERE modifierAnnotation.annotatedText = ?1 AND modifierAnnotation.modifier = ?2 ")
    List<ModifierAnnotation> listByTextAndModifier(AnnotatedText text, AnnotationModifier modifier);

    @Query("SELECT modifierAnnotation FROM ModifierAnnotation AS modifierAnnotation WHERE modifierAnnotation.annotatedText = ?1")
    List<ModifierAnnotation> findByText(AnnotatedText text);
}
