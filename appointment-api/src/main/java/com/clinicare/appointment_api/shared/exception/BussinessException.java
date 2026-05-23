package com.clinicare.appointment_api.shared.exception;

public class BussinessException extends RuntimeException {
    public BussinessException() {
        super("Erro de negócio");
    }

    public BussinessException(String message) {
        super(message);
    }
}
