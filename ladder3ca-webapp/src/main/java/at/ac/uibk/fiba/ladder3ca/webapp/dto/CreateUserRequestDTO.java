package at.ac.uibk.fiba.ladder3ca.webapp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CreateUserRequestDTO {
    public final String emailAddress;
    public final String password;
    public final List<String> roles = new ArrayList<>();

    @JsonCreator
    public CreateUserRequestDTO(
            @JsonProperty("emailAddress") String emailAddress,
            @JsonProperty("password") String password,
            @JsonProperty("roles") List<String> roles) {
        this.emailAddress = emailAddress;
        this.password = password;
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }
}
