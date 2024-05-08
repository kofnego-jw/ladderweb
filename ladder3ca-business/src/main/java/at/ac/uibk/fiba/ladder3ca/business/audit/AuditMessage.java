package at.ac.uibk.fiba.ladder3ca.business.audit;

import java.util.List;

public class AuditMessage {

    public final List<String> messages;

    public AuditMessage(List<String> messages) {
        this.messages = messages;
    }

    public AuditMessage(String msg) {
        this(List.of(msg));
    }
}
