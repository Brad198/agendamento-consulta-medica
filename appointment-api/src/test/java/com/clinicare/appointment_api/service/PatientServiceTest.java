package com.clinicare.appointment_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.repository.PatientRepository;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

@DisplayName("PatientService")
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();

        patient = new Patient();
        patient.setId(patientId);
        patient.setName("Maria Santos");
        patient.setBirthDate(LocalDate.of(1990, 5, 15));
        patient.setDocumentId("12345678900");
        patient.setPhone("11999999999");
    }

    @Nested
    @DisplayName("findPatients")
    class FindPatientsTests {

        @Test
        @DisplayName("shouldListAllPatients")
        void shouldListAllPatients() {
            // Arrange
            Patient patient2 = new Patient();
            patient2.setId(UUID.randomUUID());
            patient2.setName("João Silva");
            patient2.setBirthDate(LocalDate.of(1985, 3, 20));
            patient2.setDocumentId("98765432100");
            patient2.setPhone("11988888888");

            List<Patient> patientList = Arrays.asList(patient, patient2);
            when(patientRepository.findAll()).thenReturn(patientList);

            // Act
            List<Patient> result = patientService.findPatients();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(patientList, result);
            assertEquals("Maria Santos", result.get(0).getName());
            assertEquals("João Silva", result.get(1).getName());
            verify(patientRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("shouldReturnEmptyListWhenNoPatientsExist")
        void shouldReturnEmptyListWhenNoPatientsExist() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Arrays.asList());

            // Act
            List<Patient> result = patientService.findPatients();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.size());
            verify(patientRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findPatientById")
    class FindPatientByIdTests {

        @Test
        @DisplayName("shouldReturnPatientWhenIdExists")
        void shouldReturnPatientWhenIdExists() {
            // Arrange
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

            // Act
            Patient result = patientService.findPatientById(patientId);

            // Assert
            assertNotNull(result);
            assertEquals(patient.getId(), result.getId());
            assertEquals(patient.getName(), result.getName());
            assertEquals(patient.getBirthDate(), result.getBirthDate());
            assertEquals(patient.getDocumentId(), result.getDocumentId());
            assertEquals(patient.getPhone(), result.getPhone());
            verify(patientRepository, times(1)).findById(patientId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                patientService.findPatientById(nonExistentId);
            });
            verify(patientRepository, times(1)).findById(nonExistentId);
        }
    }
}
