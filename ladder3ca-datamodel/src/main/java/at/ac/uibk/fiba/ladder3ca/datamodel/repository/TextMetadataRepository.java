package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.TextMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextMetadataRepository extends JpaRepository<TextMetadata, String> {
}
