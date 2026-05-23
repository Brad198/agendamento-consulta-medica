package com.clinicare.appointment_api.shared.exception;

public class ForbiddenActionException extends BussinessException {
    public ForbiddenActionException(String message) {
        super(message);
    }

    public ForbiddenActionException() {
        super("Ação não autorizada");
    }
}
