package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AnnotatedText;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnotatedTextRepository extends JpaRepository<AnnotatedText, String> {

    @Query("SELECT annotatedText FROM AnnotatedText AS annotatedText WHERE annotatedText.languageCode = ?1")
    List<AnnotatedText> findAllByLanguage(String language);

    @Query("SELECT annotatedText from AnnotatedText annotatedText ORDER BY annotatedText.lastModified DESC")
    List<AnnotatedText> findNewest(PageRequest pageRequest);

    @Query("SELECT annotatedText FROM AnnotatedText annotatedText WHERE annotatedText.lastModified >= ?1 ORDER BY annotatedText.lastModified DESC")
    List<AnnotatedText> findModifiedSince(LocalDateTime lastIndexed);
}
