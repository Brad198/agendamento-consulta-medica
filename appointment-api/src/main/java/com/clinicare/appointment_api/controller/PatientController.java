package com.clinicare.appointment_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicare.appointment_api.dto.AppointmentResponseDto;
import com.clinicare.appointment_api.dto.DoctorResponseDto;
import com.clinicare.appointment_api.dto.PatientResponseDto;
import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.service.AppointmentService;
import com.clinicare.appointment_api.service.PatientService;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Endpoints para consulta e gerenciamento de pacientes do sistema")
public class PatientController {
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping()
    @Operation(summary = "Listar todos os pacientes", description = "Retorna uma lista contendo todos os pacientes cadastrados na clínica.")
    public ResponseEntity<List<PatientResponseDto>> findAllPatients() {
        List<PatientResponseDto> patients = patientService.findPatients()
                .stream()
                .map(patient -> new PatientResponseDto(
                        patient.getId(),
                        patient.getName(),
                        patient.getBirthDate(),
                        patient.getDocumentId(),
                        patient.getPhone()))
                .toList();

        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID", description = "Retorna as informações detalhadas de um paciente específico baseado no ID numérico fornecido.")
    public ResponseEntity<PatientResponseDto> findPatient(
            @PathVariable(name = "id") @Parameter(description = "ID identificador do paciente", example = "1") UUID patientId)
            throws ResourceNotFoundException {
        Patient patient = patientService.findPatientById(patientId);
        return ResponseEntity.ok().body(new PatientResponseDto(
                patient.getId(),
                patient.getName(),
                patient.getBirthDate(),
                patient.getDocumentId(),
                patient.getPhone()));
    }

    @GetMapping("/{id}/appointments")
    @Operation(summary = "Buscar consultas por ID do paciente", description = "Retorna as consultas reservadas baseado no ID do paciente.")
    public ResponseEntity<List<AppointmentResponseDto>> findAppointmentsByPatient(
            @PathVariable(name = "id") @Parameter(description = "ID identificador do paciente", example = "1") UUID patientId)
            throws ResourceNotFoundException {
        List<AppointmentResponseDto> appointments = appointmentService.findAppointmentsByPatient(patientId)
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
}
