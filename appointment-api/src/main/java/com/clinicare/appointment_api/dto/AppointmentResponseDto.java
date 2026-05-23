package com.clinicare.appointment_api.dto;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.List;

public record AppointmentResponseDto (
    Long id,
    LocalDateTime initialDatetime,
    LocalDateTime endDatetime,
    String notes,
    DoctorResponseDto doctor
) {
}

