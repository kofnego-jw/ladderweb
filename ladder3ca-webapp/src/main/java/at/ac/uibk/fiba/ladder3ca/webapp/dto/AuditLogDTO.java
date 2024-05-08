package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AuditLog;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogDTO {
    public final Long id;
    public final long creationTime;
    public final String content;
    public final Long userId;

    public AuditLogDTO(Long id, long creationTime, String content, Long userId) {
        this.id = id;
        this.creationTime = creationTime;
        this.content = content;
        this.userId = userId;
    }

    public static AuditLogDTO fromAuditLog(AuditLog log) {
        if (log == null) {
            return null;
        }
        return new AuditLogDTO(log.getId(), log.getCreationTimestamp().atZone(ZoneId.systemDefault()).toEpochSecond(), log.getLogContent(), log.getAppUserId());
    }

    public static List<AuditLogDTO> fromAuditLogs(List<AuditLog> logs) {
        if (logs == null) {
            return Collections.emptyList();
        }
        return logs.stream().map(AuditLogDTO::fromAuditLog).collect(Collectors.toList());
    }
}
