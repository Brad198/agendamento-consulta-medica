package com.clinicare.appointment_api.service;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.clinicare.appointment_api.dto.CreateAppointmentDto;
import com.clinicare.appointment_api.dto.UpdateAppointmentDto;
import com.clinicare.appointment_api.entity.Appointment;
import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.repository.AppointmentRepository;
import com.clinicare.appointment_api.repository.DoctorRepository;
import com.clinicare.appointment_api.repository.PatientRepository;
import com.clinicare.appointment_api.shared.exception.ForbiddenActionException;
import com.clinicare.appointment_api.shared.exception.ResourceAlreadyExistsException;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AppointmentService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(DoctorRepository doctorRepository, PatientRepository patientRepository,
            AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment findAppointmentById(Long appointmentId)
            throws ResourceNotFoundException {
        return this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
    }

    public List<Appointment> findAppointments() {
        return this.appointmentRepository.findAll();
    }

    public List<Appointment> findAppointmentsByPatient(UUID patientId) {
        return this.appointmentRepository.findAllByPatientId(patientId);
    }

    @Transactional
    public Appointment createAppointment(CreateAppointmentDto appointmentDto)
            throws ResourceAlreadyExistsException, ResourceNotFoundException {
        log.info("Iniciando criação de agendamento. Médico ID: {}, Paciente ID: {}, Início: {}",
                appointmentDto.doctorId(), appointmentDto.patientId(), appointmentDto.initialDatetime());

        Doctor doctor = doctorRepository.findWithLockById(appointmentDto.doctorId())
                .orElseThrow(() -> {
                    log.warn("Falha no agendamento: Médico ID: {} não encontrado", appointmentDto.doctorId());
                    return new ResourceNotFoundException("Médico não encontrado");
                });

        Patient patient = patientRepository.findById(appointmentDto.patientId())
                .orElseThrow(() -> {
                    log.warn("Falha no agendamento: Paciente ID: {} não encontrado", appointmentDto.patientId());
                    return new ResourceNotFoundException("Paciente não encontrado");
                });

        boolean existsAppointment = appointmentRepository.existsOverlap(
                appointmentDto.doctorId(),
                appointmentDto.initialDatetime(),
                appointmentDto.endDatetime());

        if (existsAppointment) {
            log.warn("Conflito de horário detectado para o Médico ID: {} entre {} e {}",
                    appointmentDto.doctorId(), appointmentDto.initialDatetime(), appointmentDto.endDatetime());
            throw new ResourceAlreadyExistsException("Este horário já está ocupado para este médico.");
        }


        // 3. Salva a consulta
        Appointment newAppointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .initialDatetime(appointmentDto.initialDatetime())
                .endDatetime(appointmentDto.endDatetime())
                .notes(appointmentDto.notes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);
        log.info("Consulta criada com sucesso! Novo Agendamento ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Transactional
    public void updateAppointmentById(Long appointmentId, UpdateAppointmentDto appointmentDto)
            throws ResourceNotFoundException, ForbiddenActionException {
        log.info("Iniciando atualização da consulta ID: {}. Novo Médico ID: {}", appointmentId, appointmentDto.doctorId());

        Appointment currentAppointment = this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Consulta ID: {} não encontrada", appointmentId);
                    return new ResourceNotFoundException("Consulta não encontrada");
                });

        if (!currentAppointment.getPatient().getId().equals(appointmentDto.patientId())) {
            log.error("VIOLAÇÃO DE SEGURANÇA: Paciente ID: {} tentou alterar a consulta ID: {} pertencente a outro paciente",
                    appointmentDto.patientId(), appointmentId);
            throw new ForbiddenActionException("Você não tem permissão para atualizar esta consulta");
        }

        Doctor doctor = doctorRepository.findWithLockById(appointmentDto.doctorId())
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Médico ID: {} não encontrado", appointmentDto.doctorId());
                    return new ResourceNotFoundException("Médico não encontrado");
                });

        boolean existsAppointment = appointmentRepository.existsOverlapAtUpdate(
                appointmentDto.doctorId(),
                appointmentId,
                appointmentDto.initialDatetime(),
                appointmentDto.endDatetime());


        if (existsAppointment) {
            log.warn("Falha na atualização: Novo horário indisponível para o Médico ID: {}", appointmentDto.doctorId());
            throw new ResourceAlreadyExistsException("Este horário já está ocupado para este médico.");
        }

        currentAppointment.setDoctor(doctor);
        currentAppointment.setInitialDatetime(appointmentDto.initialDatetime());
        currentAppointment.setEndDatetime(appointmentDto.endDatetime());
        currentAppointment.setNotes(appointmentDto.notes());

        this.appointmentRepository.save(currentAppointment);
        log.info("Consulta ID: {} atualizada com sucesso", appointmentId);
    }

    @Transactional
    public void deleteAppointmentById(UUID patientId, Long appointmentId)
            throws ResourceNotFoundException, ForbiddenActionException {
        log.info("Iniciando exclusão da consulta ID: {} pelo Paciente ID: {}", appointmentId, patientId);

        Appointment currentAppointment = this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Falha na exclusão: Consulta ID: {} não encontrada", appointmentId);
                    return new ResourceNotFoundException("Consulta não encontrada");
                });

        if (!currentAppointment.getPatient().getId().equals(patientId)) {
            log.error("VIOLAÇÃO DE SEGURANÇA: Paciente ID: {} tentou deletar a consulta ID: {} de outro proprietário",
                    patientId, appointmentId);
            throw new ForbiddenActionException("Você não tem permissão para excluir esta consulta");
        }

        this.appointmentRepository.deleteById(appointmentId);
        log.info("Consulta ID: {} excluída com sucesso", appointmentId);
    }
}
