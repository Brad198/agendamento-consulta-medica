package com.clinicare.appointment_api.shared.exception;

public class ResourceAlreadyExistsException extends BussinessException {
    public ResourceAlreadyExistsException() {
        super("Recurso já existe");
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
