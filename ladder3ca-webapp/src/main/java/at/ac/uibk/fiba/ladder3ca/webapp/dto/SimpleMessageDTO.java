package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleMessageDTO {

    public static final SimpleMessageDTO OKAY = new SimpleMessageDTO(200, "okay", Collections.emptyList());

    public final Integer resultCode;
    public final String message;
    public final List<String> errorMessages;

    public SimpleMessageDTO(Integer resultCode, String message, List<String> errorMessages) {
        this.resultCode = resultCode;
        this.message = message;
        this.errorMessages = errorMessages;
    }

    public static SimpleMessageDTO errorMessage(Integer resultCode, String message, Throwable exception) {
        List<String> errorMsgs = new ArrayList<>();
        Throwable th = exception;
        do {
            errorMsgs.add(th.getMessage());
            th = th.getCause();
        } while (th != null);
        return new SimpleMessageDTO(resultCode, message, errorMsgs);
    }

}
