package com.clinicare.appointment_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.repository.DoctorRepository;
import com.clinicare.appointment_api.shared.enums.SpecialtyEnum;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;

@DisplayName("DoctorService")
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setName("Dr. João Silva");
        doctor.setSpecialty(SpecialtyEnum.CARDIOLOGY);
    }

    @Nested
    @DisplayName("findDoctors")
    class FindDoctorsTests {

        @Test
        @DisplayName("shouldListAllDoctors")
        void shouldListAllDoctors() {
            // Arrange
            Doctor doctor2 = new Doctor();
            doctor2.setId(UUID.randomUUID());
            doctor2.setName("Dra. Maria Santos");
            doctor2.setSpecialty(SpecialtyEnum.NEUROLOGY);

            List<Doctor> doctorList = Arrays.asList(doctor, doctor2);
            when(doctorRepository.findAll()).thenReturn(doctorList);

            // Act
            List<Doctor> result = doctorService.findDoctors();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(doctorList, result);
            assertEquals("Dr. João Silva", result.get(0).getName());
            assertEquals("Dra. Maria Santos", result.get(1).getName());
            verify(doctorRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("shouldReturnEmptyListWhenNoDoctorsExist")
        void shouldReturnEmptyListWhenNoDoctorsExist() {
            // Arrange
            when(doctorRepository.findAll()).thenReturn(Arrays.asList());

            // Act
            List<Doctor> result = doctorService.findDoctors();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.size());
            verify(doctorRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("findDoctorById")
    class FindDoctorByIdTests {

        @Test
        @DisplayName("shouldReturnDoctorWhenIdExists")
        void shouldReturnDoctorWhenIdExists() {
            // Arrange
            when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

            // Act
            Doctor result = doctorService.findDoctorById(doctorId);

            // Assert
            assertNotNull(result);
            assertEquals(doctor.getId(), result.getId());
            assertEquals(doctor.getName(), result.getName());
            assertEquals(doctor.getSpecialty(), result.getSpecialty());
            verify(doctorRepository, times(1)).findById(doctorId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist")
        void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(doctorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                doctorService.findDoctorById(nonExistentId);
            });
            verify(doctorRepository, times(1)).findById(nonExistentId);
        }
    }
}
