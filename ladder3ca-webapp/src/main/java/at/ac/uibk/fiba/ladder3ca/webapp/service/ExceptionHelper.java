package at.ac.uibk.fiba.ladder3ca.webapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExceptionHelper {

    public static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public static ResponseStatusException internalError(String reason, Throwable e) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, e);
    }
}
