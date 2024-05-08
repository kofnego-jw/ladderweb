package at.ac.uibk.fiba.ladder3ca.business.audit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuditErrorMessage {

    public final String className;
    public final List<String> messages;
    public final List<String> exceptionStacks;

    @JsonCreator
    public AuditErrorMessage(@JsonProperty("className") String className,
                             @JsonProperty("messages") List<String> messages,
                             @JsonProperty("exceptionStacks") List<String> exceptionStacks) {
        this.className = className;
        this.messages = messages;
        this.exceptionStacks = exceptionStacks;
    }

    public AuditErrorMessage(Throwable th) {
        if (th.getStackTrace() != null && th.getStackTrace().length > 0) {
            this.className = th.getStackTrace()[0].getClassName();
            this.exceptionStacks = Stream.of(th.getStackTrace())
                    .map(StackTraceElement::toString).collect(Collectors.toList());
            this.messages = new ArrayList<>();
            Throwable cause = th;
            do {
                messages.add(cause.getMessage());
                cause = cause.getCause();
            } while (cause != null);
        } else {
            this.className = th.getClass().getName();
            this.messages = List.of(th.getMessage());
            this.exceptionStacks = Collections.emptyList();
        }

    }
}
