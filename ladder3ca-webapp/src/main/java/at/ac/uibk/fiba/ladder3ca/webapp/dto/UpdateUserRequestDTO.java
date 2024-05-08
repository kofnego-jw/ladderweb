package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserRequestDTO {
    public final Long userId;
    public final String emailAddress;
    public final List<String> roles = new ArrayList<>();

    @JsonCreator
    public UpdateUserRequestDTO(@JsonProperty("userId") Long userId,
                                @JsonProperty("emailAddress") String emailAddress,
                                @JsonProperty("roles") List<String> roles) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }
}
