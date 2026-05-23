package com.clinicare.appointment_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicare.appointment_api.dto.CreateAppointmentDto;
import com.clinicare.appointment_api.dto.UpdateAppointmentDto;
import com.clinicare.appointment_api.entity.Appointment;
import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.repository.AppointmentRepository;
import com.clinicare.appointment_api.repository.DoctorRepository;
import com.clinicare.appointment_api.repository.PatientRepository;
import com.clinicare.appointment_api.shared.enums.SpecialtyEnum;
import com.clinicare.appointment_api.shared.exception.ForbiddenActionException;
import com.clinicare.appointment_api.shared.exception.ResourceAlreadyExistsException;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

@DisplayName("AppointmentService")
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor doctor;
    private Patient patient;
    private Appointment appointment;
    private LocalDateTime futureDateTime;

    @BeforeEach
    void setUp() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setName("Dr. João Silva");
        doctor.setSpecialty(SpecialtyEnum.CARDIOLOGY);

        patient = new Patient();
        patient.setId(patientId);
        patient.setName("Maria Santos");
        patient.setBirthDate(LocalDate.of(1990, 5, 15));
        patient.setDocumentId("12345678900");
        patient.setPhone("11999999999");

        futureDateTime = LocalDateTime.now().plusDays(7);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setInitialDatetime(futureDateTime);
        appointment.setEndDatetime(futureDateTime.plusMinutes(30));
        appointment.setNotes("Consulta de rotina");
    }

    @Nested
    @DisplayName("findAppointmentById")
    class FindAppointmentByIdTests {

        @Test
        @DisplayName("shouldReturnAppointmentWhenIdExists")
        void shouldReturnAppointmentWhenIdExists() {
            // Arrange
            Long appointmentId = 1L;
            when(appointmentRepository.findById(appointmentId))
                    .thenReturn(Optional.of(appointment));

            // Act
            Appointment result = appointmentService.findAppointmentById(appointmentId);

            // Assert
            assertNotNull(result);
            assertEquals(appointment.getId(), result.getId());
            assertEquals(appointment.getDoctor().getId(), result.getDoctor().getId());
            assertEquals(appointment.getPatient().getId(), result.getPatient().getId());
            verify(appointmentRepository, times(1)).findById(appointmentId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
            // Arrange
            Long appointmentId = 999L;
            when(appointmentRepository.findById(appointmentId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.findAppointmentById(appointmentId);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
        }
    }

    @Nested
    @DisplayName("findAppointments")
    class FindAppointmentsTests {

        @Test
        @DisplayName("shouldReturnListOfAppointments")
        void shouldReturnListOfAppointments() {
            // Arrange
            Appointment appointment2 = new Appointment();
            appointment2.setId(2L);
            appointment2.setDoctor(doctor);
            appointment2.setPatient(patient);
            appointment2.setInitialDatetime(futureDateTime.plusDays(1));
            appointment2.setEndDatetime(futureDateTime.plusDays(1).plusMinutes(30));
            appointment2.setNotes("Consulta de retorno");

            List<Appointment> appointmentList = Arrays.asList(appointment, appointment2);
            when(appointmentRepository.findAll()).thenReturn(appointmentList);

            // Act
            List<Appointment> result = appointmentService.findAppointments();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(appointmentList, result);
            verify(appointmentRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("shouldReturnEmptyListWhenNoAppointmentsExist")
        void shouldReturnEmptyListWhenNoAppointmentsExist() {
            // Arrange
            when(appointmentRepository.findAll()).thenReturn(Arrays.asList());

            // Act
            List<Appointment> result = appointmentService.findAppointments();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.size());
            verify(appointmentRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findAppointmentsByPatient")
    class FindAppointmentsByPatientTests {

        @Test
        @DisplayName("shouldReturnAppointmentsForPatient")
        void shouldReturnAppointmentsForPatient() {
            // Arrange
            UUID patientId = patient.getId();
            Appointment appointment2 = new Appointment();
            appointment2.setId(2L);
            appointment2.setDoctor(doctor);
            appointment2.setPatient(patient);
            appointment2.setInitialDatetime(futureDateTime.plusDays(1));
            appointment2.setEndDatetime(futureDateTime.plusDays(1).plusMinutes(30));
            appointment2.setNotes("Consulta de retorno");

            List<Appointment> appointmentList = Arrays.asList(appointment, appointment2);
            when(appointmentRepository.findAllByPatientId(patientId)).thenReturn(appointmentList);

            // Act
            List<Appointment> result = appointmentService.findAppointmentsByPatient(patientId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(appointmentList, result);
            verify(appointmentRepository, times(1)).findAllByPatientId(patientId);
        }
    }

    @Nested
    @DisplayName("createAppointment")
    class CreateAppointmentTests {

        @Test
        @DisplayName("shouldCreateAppointmentSuccessfully")
        void shouldCreateAppointmentSuccessfully() {
            // Arrange
            CreateAppointmentDto createDto = new CreateAppointmentDto(
                    doctor.getId(),
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Exame de rotina");

            when(appointmentRepository.existsOverlap(
                    doctor.getId(), futureDateTime, futureDateTime.plusMinutes(30))).thenReturn(false);
            when(doctorRepository.findWithLockById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

            // Act
            Appointment result = appointmentService.createAppointment(createDto);

            // Assert
            assertNotNull(result);
            assertEquals(doctor.getId(), result.getDoctor().getId());
            assertEquals(patient.getId(), result.getPatient().getId());
            assertEquals(futureDateTime, result.getInitialDatetime());
            assertEquals(futureDateTime.plusMinutes(30), result.getEndDatetime());
            verify(doctorRepository, times(1)).findWithLockById(doctor.getId());
            verify(patientRepository, times(1)).findById(patient.getId());
            verify(appointmentRepository, times(1)).existsOverlap(
                    doctor.getId(), futureDateTime, futureDateTime.plusMinutes(30));
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("shouldThrowResourceAlreadyExistsExceptionWhenTimeSlotIsTaken")
        void shouldThrowResourceAlreadyExistsExceptionWhenTimeSlotIsTaken() {
            // Arrange
            CreateAppointmentDto createDto = new CreateAppointmentDto(
                    doctor.getId(),
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Exame de rotina");

            when(doctorRepository.findWithLockById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(appointmentRepository.existsOverlap(
                    doctor.getId(), futureDateTime, futureDateTime.plusMinutes(30))).thenReturn(true);

            // Act & Assert
            assertThrows(ResourceAlreadyExistsException.class, () -> {
                appointmentService.createAppointment(createDto);
            });
            verify(doctorRepository, times(1)).findWithLockById(doctor.getId());
            verify(patientRepository, times(1)).findById(patient.getId());
            verify(appointmentRepository, times(1)).existsOverlap(
                    doctor.getId(), futureDateTime, futureDateTime.plusMinutes(30));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenDoctorDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenDoctorDoesNotExist() {
            // Arrange
            CreateAppointmentDto createDto = new CreateAppointmentDto(
                    doctor.getId(),
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Exame de rotina");

            when(doctorRepository.findWithLockById(doctor.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.createAppointment(createDto);
            });
            verify(doctorRepository, times(1)).findWithLockById(doctor.getId());
            verify(patientRepository, never()).findById(any());
            verify(appointmentRepository, never()).existsOverlap(any(), any(), any());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {
            // Arrange
            CreateAppointmentDto createDto = new CreateAppointmentDto(
                    doctor.getId(),
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Exame de rotina");

            when(doctorRepository.findWithLockById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.createAppointment(createDto);
            });
            verify(doctorRepository, times(1)).findWithLockById(doctor.getId());
            verify(patientRepository, times(1)).findById(patient.getId());
            verify(appointmentRepository, never()).existsOverlap(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("updateAppointmentById")
    class UpdateAppointmentByIdTests {

        @Test
        @DisplayName("shouldUpdateAppointmentSuccessfully")
        void shouldUpdateAppointmentSuccessfully() {
            // Arrange
            Long appointmentId = 1L;
            LocalDateTime newDateTime = futureDateTime.plusDays(2);
            Doctor newDoctor = new Doctor();
            newDoctor.setId(UUID.randomUUID());
            newDoctor.setName("Dr. Pedro Costa");
            newDoctor.setSpecialty(SpecialtyEnum.NEUROLOGY);

            UpdateAppointmentDto updateDto = new UpdateAppointmentDto(
                    newDoctor.getId(),
                    patient.getId(),
                    newDateTime,
                    newDateTime.plusMinutes(30),
                    "Ajuste de horário");

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.existsOverlapAtUpdate(
                    newDoctor.getId(), appointmentId, newDateTime, newDateTime.plusMinutes(30))).thenReturn(false);
            when(doctorRepository.findWithLockById(newDoctor.getId())).thenReturn(Optional.of(newDoctor));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

            // Act
            appointmentService.updateAppointmentById(appointmentId, updateDto);

            // Assert
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(doctorRepository, times(1)).findWithLockById(newDoctor.getId());
            verify(appointmentRepository, times(1)).existsOverlapAtUpdate(
                    newDoctor.getId(), appointmentId, newDateTime, newDateTime.plusMinutes(30));
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist() {
            // Arrange
            Long appointmentId = 999L;
            UpdateAppointmentDto updateDto = new UpdateAppointmentDto(
                    doctor.getId(),
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Ajuste de horário");

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.updateAppointmentById(appointmentId, updateDto);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(doctorRepository, never()).findById(any());
        }

        @Test
        @DisplayName("shouldThrowForbiddenActionExceptionWhenPatientIsNotTheOwner")
        void shouldThrowForbiddenActionExceptionWhenPatientIsNotTheOwner() {
            // Arrange
            Long appointmentId = 1L;
            UUID differentPatientId = UUID.randomUUID();
            UpdateAppointmentDto updateDto = new UpdateAppointmentDto(
                    doctor.getId(),
                    differentPatientId,
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Tentativa de alteração indevida");

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

            // Act & Assert
            assertThrows(ForbiddenActionException.class, () -> {
                appointmentService.updateAppointmentById(appointmentId, updateDto);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(doctorRepository, never()).findById(any());
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenNewDoctorDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenNewDoctorDoesNotExist() {
            // Arrange
            Long appointmentId = 1L;
            UUID nonExistentDoctorId = UUID.randomUUID();
            UpdateAppointmentDto updateDto = new UpdateAppointmentDto(
                    nonExistentDoctorId,
                    patient.getId(),
                    futureDateTime,
                    futureDateTime.plusMinutes(30),
                    "Ajuste para novo médico");

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(doctorRepository.findWithLockById(nonExistentDoctorId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.updateAppointmentById(appointmentId, updateDto);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(doctorRepository, times(1)).findWithLockById(nonExistentDoctorId);
            verify(appointmentRepository, never()).existsOverlapAtUpdate(any(), any(Long.class), any(), any());
            verify(appointmentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteAppointmentById")
    class DeleteAppointmentByIdTests {

        @Test
        @DisplayName("shouldDeleteAppointmentSuccessfully")
        void shouldDeleteAppointmentSuccessfully() {
            // Arrange
            Long appointmentId = 1L;
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            doNothing().when(appointmentRepository).deleteById(appointmentId);

            // Act
            appointmentService.deleteAppointmentById(patient.getId(), appointmentId);

            // Assert
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(appointmentRepository, times(1)).deleteById(appointmentId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist() {
            // Arrange
            Long appointmentId = 999L;
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                appointmentService.deleteAppointmentById(patient.getId(), appointmentId);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(appointmentRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("shouldThrowForbiddenActionExceptionWhenPatientIsNotTheOwner")
        void shouldThrowForbiddenActionExceptionWhenPatientIsNotTheOwner() {
            // Arrange
            Long appointmentId = 1L;
            UUID differentPatientId = UUID.randomUUID();
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

            // Act & Assert
            assertThrows(ForbiddenActionException.class, () -> {
                appointmentService.deleteAppointmentById(differentPatientId, appointmentId);
            });
            verify(appointmentRepository, times(1)).findById(appointmentId);
            verify(appointmentRepository, never()).deleteById(any());
        }
    }
}
