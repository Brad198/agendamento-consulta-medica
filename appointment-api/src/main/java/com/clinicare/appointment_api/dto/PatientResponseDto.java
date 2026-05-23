package com.clinicare.appointment_api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponseDto(
    UUID id,
    String name,
    LocalDate birthDate,
    String documentId,
    String phone
) {
}
