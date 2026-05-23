package com.clinicare.appointment_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicare.appointment_api.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
