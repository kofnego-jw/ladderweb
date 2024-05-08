package at.ac.uibk.fiba.ladder3ca.business.audit;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AuditLog;
import at.ac.uibk.fiba.ladder3ca.datamodel.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogService {

    public static final int MAX_AUDIT_LENGTH = 5000;

    private final AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public List<AuditLog> getLogsSince(LocalDateTime begin) {
        PageRequest pageRequest = PageRequest.of(0, MAX_AUDIT_LENGTH);
        return auditLogRepository.findAllSince(begin, pageRequest);
    }

    public List<AuditLog> getLogByUser(AppUser user) {
        return auditLogRepository.findAllByUser(user.getId());
    }

    public AuditLog log(String message) {
        return log(message, null);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog log(String message, AppUser user) {
        Long id = user == null ? null : user.getId();
        try {
            String s = objectMapper.writeValueAsString(new AuditMessage(message));
            AuditLog log = new AuditLog(s, id);
            return auditLogRepository.save(log);
        } catch (Exception e) {
            AuditLog log = new AuditLog(message + "\\nAdditional error: " + e.getMessage(), id);
            return auditLogRepository.save(log);
        }
    }

    public AuditLog log(Throwable th) {
        return log(th, null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog log(Throwable throwable, AppUser user) {
        Long userId = user == null ? null : user.getId();
        try {
            String s = objectMapper.writeValueAsString(new AuditErrorMessage(throwable));
            AuditLog log = new AuditLog(s, userId);
            return auditLogRepository.save(log);
        } catch (Exception e) {
            AuditLog log = new AuditLog(throwable.getMessage() + "\\nAdditional error: " + e.getMessage(), userId);
            return auditLogRepository.save(log);
        }
    }


}
