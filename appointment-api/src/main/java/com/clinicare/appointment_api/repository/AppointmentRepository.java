package com.clinicare.appointment_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinicare.appointment_api.entity.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByPatientId(UUID patientId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE "
            + "a.doctor.id = :doctorId AND "
            + "a.initialDatetime < :end AND "
            + "a.endDatetime > :start")
    boolean existsOverlap(@Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime initialDatetime, @Param("end") LocalDateTime endDatetime);

    // Valida overlap ignorando o ID da consulta atual
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE "
            + "a.doctor.id = :doctorId AND "
            + "a.id <> :currentAppointmentId AND"
            + "(:initial < a.endDatetime AND :end > a.initialDatetime)")
    boolean existsOverlapAtUpdate(UUID doctorId, Long currentAppointmentId, LocalDateTime initial, LocalDateTime end);
}
