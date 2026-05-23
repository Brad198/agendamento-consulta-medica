package com.clinicare.appointment_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.repository.PatientRepository;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findPatients() {
        return this.patientRepository.findAll();
    }

    public Patient findPatientById(UUID id) throws ResourceNotFoundException {
        return this.patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));
    }
}
