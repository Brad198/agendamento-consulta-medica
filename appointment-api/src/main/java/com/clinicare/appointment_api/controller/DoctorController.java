package com.clinicare.appointment_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicare.appointment_api.dto.DoctorResponseDto;
import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.service.DoctorService;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctors", description = "Endpoints para consulta e gerenciamento de médicos do sistema")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping()
    @Operation(summary = "Listar todos os médicos", description = "Retorna uma lista contendo todos os médicos cadastrados na clínica.")
    public ResponseEntity<List<DoctorResponseDto>> findAllDoctors() {
        List<DoctorResponseDto> doctors = doctorService.findDoctors()
                .stream()
                .map(doctor -> new DoctorResponseDto(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialty()))
                .toList();
        return ResponseEntity.ok().body(doctors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID", description = "Retorna as informações detalhadas de um médico específico baseado no ID numérico fornecido.")
    public ResponseEntity<DoctorResponseDto> findDoctor(
            @PathVariable(name = "id") @Parameter(description = "ID identificador do médico", example = "1") UUID doctorId)
            throws ResourceNotFoundException {
        Doctor doctor = doctorService.findDoctorById(doctorId);
        return ResponseEntity.ok().body(new DoctorResponseDto(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty()));
    }
}
