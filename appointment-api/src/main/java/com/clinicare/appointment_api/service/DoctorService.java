package com.clinicare.appointment_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.repository.DoctorRepository;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> findDoctors() {
        return this.doctorRepository.findAll();
    }

    public Doctor findDoctorById(UUID id) throws ResourceNotFoundException {
        return this.doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));
    }
}
