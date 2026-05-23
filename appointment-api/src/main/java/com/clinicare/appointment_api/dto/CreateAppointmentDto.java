package com.clinicare.appointment_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.clinicare.appointment_api.shared.validations.StartBeforeEnd;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@StartBeforeEnd(message = "A data e hora de término deve ser maior que a inicial.")
public record CreateAppointmentDto(
        @NotNull(message = "O ID do médico é obrigatório.")
        UUID doctorId,

        @NotNull(message = "O ID do paciente não pode estar vazio.")
        UUID patientId,

        @NotNull(message = "A data e hora inicial da consulta é obrigatória.")
        @Future(message = "O agendamento deve ser uma data futura.")
        LocalDateTime initialDatetime,

        @NotNull(message = "A data e hora final da consulta é obrigatória.")
        LocalDateTime endDatetime,

        @Size(max = 255, message = "O campo de notas deve ter no máximo 255 caracteres.")
        String notes
) {
}
