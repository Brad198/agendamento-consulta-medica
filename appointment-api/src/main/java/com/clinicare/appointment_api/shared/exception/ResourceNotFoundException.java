package com.clinicare.appointment_api.shared.exception;

public class ResourceNotFoundException extends BussinessException {
    public ResourceNotFoundException() {
        super("Recurso não encontrado");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}