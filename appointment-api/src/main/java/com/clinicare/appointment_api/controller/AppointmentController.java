package com.clinicare.appointment_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.clinicare.appointment_api.dto.AppointmentResponseDto;
import com.clinicare.appointment_api.dto.CreateAppointmentDto;
import com.clinicare.appointment_api.dto.DoctorResponseDto;
import com.clinicare.appointment_api.dto.UpdateAppointmentDto;
import com.clinicare.appointment_api.entity.Appointment;
import com.clinicare.appointment_api.service.AppointmentService;
import com.clinicare.appointment_api.shared.exception.ForbiddenActionException;
import com.clinicare.appointment_api.shared.exception.ResourceAlreadyExistsException;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Endpoints para gerenciamento de consultas médicas")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping()
    @Operation(summary = "Listar todos os agendamentos", description = "Retorna uma lista contendo todos os agendamentos cadastrados.")
    public ResponseEntity<List<AppointmentResponseDto>> findAllAppointments() {
        List<AppointmentResponseDto> appointments = appointmentService.findAppointments()
                .stream()
                .map(appointment -> new AppointmentResponseDto(
                        appointment.getId(),
                        appointment.getInitialDatetime(),
                        appointment.getEndDatetime(),
                        appointment.getNotes(),
                        new DoctorResponseDto(
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getDoctor().getSpecialty())))
                .toList();
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID", description = "Retorna os detalhes de um agendamento específico baseado no UUID fornecido.")
    public ResponseEntity<AppointmentResponseDto> findAppointment(
            @PathVariable(name = "id") @Parameter(description = "UUID do agendamento", example = "123e4567-e89b-12d3-a456-426614174000") Long appointmentId)
            throws ResourceNotFoundException {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        return ResponseEntity.ok().body(new AppointmentResponseDto(
                appointment.getId(),
                appointment.getInitialDatetime(),
                appointment.getEndDatetime(),
                appointment.getNotes(),
                new DoctorResponseDto(
                        appointment.getDoctor().getId(),
                        appointment.getDoctor().getName(),
                        appointment.getDoctor().getSpecialty())));
    }

    @PostMapping()
    @Operation(summary = "Criar um novo agendamento", description = "Salva uma nova consulta no sistema. Valida se o médico ou paciente já possuem conflito de horário.")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @RequestBody @Valid CreateAppointmentDto appointmentDto)
            throws ResourceAlreadyExistsException, ResourceNotFoundException {
        Appointment newAppointment = appointmentService.createAppointment(appointmentDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAppointment.getId())
                .toUri();

        return ResponseEntity.created(location).body(new AppointmentResponseDto(
                newAppointment.getId(),
                newAppointment.getInitialDatetime(),
                newAppointment.getEndDatetime(),
                newAppointment.getNotes(),
                new DoctorResponseDto(
                        newAppointment.getDoctor().getId(),
                        newAppointment.getDoctor().getName(),
                        newAppointment.getDoctor().getSpecialty())));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente um agendamento", description = "Modifica atributos específicos de um agendamento existente.")
    public ResponseEntity<Void> updateAppointment(
            @PathVariable(name = "id") @Parameter(description = "UUID do agendamento a ser atualizado") Long appointmentId,
            @RequestBody @Valid UpdateAppointmentDto appointmentDto)
            throws ResourceNotFoundException, ForbiddenActionException {
        appointmentService.updateAppointmentById(appointmentId, appointmentDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{appointmentId}/patients/{patientId}")
    @Operation(summary = "Cancelar um agendamento dado um paciente", description = "Cancela um agendamento existente e pertencente ao paciente.")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable(name = "appointmentId") @Parameter(description = "Id do agendamento a ser cancelado") Long appointmentId,
            @PathVariable(name = "patientId") @Parameter(description = "UUID do agendamento a ser cancelado") UUID patientId)
            throws ResourceNotFoundException, ForbiddenActionException {
        appointmentService.deleteAppointmentById(patientId, appointmentId);
        return ResponseEntity.noContent().build();
    }
}
