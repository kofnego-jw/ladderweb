package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotationSubact;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.SubactAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubactAnnotationRepository extends JpaRepository<SubactAnnotation, Long> {


    @Query("SELECT subactAnnotation FROM SubactAnnotation AS subactAnnotation WHERE subactAnnotation.annotatedText = ?1 AND subactAnnotation.subact = ?2 ")
    List<SubactAnnotation> findByTextAndSubact(AnnotatedText text, AnnotationSubact subact);

    @Query("SELECT subactAnnotation FROM SubactAnnotation AS subactAnnotation WHERE subactAnnotation.annotatedText = ?1")
    List<SubactAnnotation> findByText(AnnotatedText text);
}
