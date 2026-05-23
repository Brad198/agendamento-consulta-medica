package com.clinicare.appointment_api.shared.validations;

import com.clinicare.appointment_api.dto.CreateAppointmentDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, CreateAppointmentDto> {
    @Override
    public boolean isValid(CreateAppointmentDto dto, ConstraintValidatorContext context) {
        if (dto.initialDatetime() == null || dto.endDatetime() == null) {
            return true;
        }

        return dto.endDatetime().isAfter(dto.initialDatetime());
    }
}