package com.clinicare.appointment_api.dto;

import com.clinicare.appointment_api.shared.enums.SpecialtyEnum;

import java.util.UUID;

public record DoctorResponseDto(
        UUID id,
        String name,
        SpecialtyEnum specialty
) {
}
