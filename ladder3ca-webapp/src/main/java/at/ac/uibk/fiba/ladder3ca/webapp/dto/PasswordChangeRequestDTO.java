package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordChangeRequestDTO {

    public final String oldPassword;
    public final String newPassword;
    public final Long userId;

    @JsonCreator
    public PasswordChangeRequestDTO(
            @JsonProperty("oldPassword") String oldPassword,
            @JsonProperty("newPassword") String newPassword,
            @JsonProperty("userId") Long userId) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.userId = userId;
    }
}
