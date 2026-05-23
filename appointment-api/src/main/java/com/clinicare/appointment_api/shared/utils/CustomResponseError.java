package com.clinicare.appointment_api.shared.utils;

import java.time.LocalDateTime;

public record CustomResponseError<T>(
        Integer status,
        String message,
        LocalDateTime timestamp,
        T error) {
}
