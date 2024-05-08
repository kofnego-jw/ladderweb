package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AuditLog;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT auditLog FROM AuditLog auditLog WHERE auditLog.creationTimestamp >= ?1 ORDER BY auditLog.creationTimestamp DESC")
    List<AuditLog> findAllSince(LocalDateTime begin, PageRequest pageRequest);

    @Query("SELECT auditLog FROM AuditLog auditLog WHERE auditLog.appUserId = ?1 ORDER BY auditLog.creationTimestamp DESC")
    List<AuditLog> findAllByUser(Long userId);
}
