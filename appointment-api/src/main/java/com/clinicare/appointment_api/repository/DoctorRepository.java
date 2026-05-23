package com.clinicare.appointment_api.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.clinicare.appointment_api.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    // Bloqueia o médico para evitar concorrência
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Doctor> findWithLockById(UUID doctorId);
}
