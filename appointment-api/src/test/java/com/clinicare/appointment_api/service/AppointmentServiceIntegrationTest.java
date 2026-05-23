package com.clinicare.appointment_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.clinicare.appointment_api.dto.CreateAppointmentDto;
import com.clinicare.appointment_api.dto.UpdateAppointmentDto;
import com.clinicare.appointment_api.entity.Appointment;
import com.clinicare.appointment_api.entity.Doctor;
import com.clinicare.appointment_api.entity.Patient;
import com.clinicare.appointment_api.repository.AppointmentRepository;
import com.clinicare.appointment_api.repository.DoctorRepository;
import com.clinicare.appointment_api.repository.PatientRepository;
import com.clinicare.appointment_api.shared.enums.SpecialtyEnum;
import com.clinicare.appointment_api.shared.exception.ResourceAlreadyExistsException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AppointmentServiceIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    public void testConcurrentCreateAppointmentWithPessimisticLock() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Dr Concurrent");
        doctor.setSpecialty(SpecialtyEnum.GENERAL_PRACTICE);
        doctor = doctorRepository.save(doctor);

        Patient patient = new Patient();
        patient.setName("Patient A");
        patient.setBirthDate(java.time.LocalDate.now().minusYears(30));
        patient.setDocumentId("DOC123");
        patient.setPhone("999999999");
        patient = patientRepository.save(patient);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(30);

        CreateAppointmentDto dto = new CreateAppointmentDto(doctor.getId(), patient.getId(), start, end, "notes");

        int threads = 2;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                ready.countDown();
                startLatch.await();
                try {
                    Appointment a = appointmentService.createAppointment(dto);
                    return "OK:" + a.getId();
                } catch (ResourceAlreadyExistsException e) {
                    return "CONFLICT";
                }
            });
        }

        List<Future<String>> futures = new ArrayList<>();
        for (Callable<String> t : tasks) {
            futures.add(exec.submit(t));
        }

        // wait for threads ready
        ready.await();
        startLatch.countDown();

        int ok = 0;
        int conflict = 0;
        for (Future<String> f : futures) {
            String res = f.get();
            if (res.startsWith("OK:"))
                ok++;
            if ("CONFLICT".equals(res))
                conflict++;
        }

        exec.shutdownNow();

        assertEquals(1, ok, "Apenas uma criação deve ser bem-sucedida");
        assertEquals(1, conflict, "Uma chamada deve falhar por conflito de horário");
        assertEquals(1, appointmentRepository.findAllByPatientId(patient.getId()).size(),
                "Deve existir apenas 1 agendamento para este paciente");
    }

    @Test
    public void testConcurrentUpdateAppointmentWithPessimisticLock() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Dr Update");
        doctor.setSpecialty(SpecialtyEnum.GENERAL_PRACTICE);
        doctor = doctorRepository.save(doctor);

        Patient p1 = new Patient();
        p1.setName("P1");
        p1.setBirthDate(java.time.LocalDate.now().minusYears(25));
        p1.setDocumentId("DOCP1");
        p1.setPhone("999111111");
        p1 = patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setName("P2");
        p2.setBirthDate(java.time.LocalDate.now().minusYears(28));
        p2.setDocumentId("DOCP2");
        p2.setPhone("999222222");
        p2 = patientRepository.save(p2);

        // create two appointments at different times
        LocalDateTime now = LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0);
        Appointment a1 = Appointment.builder().doctor(doctor).patient(p1).initialDatetime(now)
                .endDatetime(now.plusMinutes(30)).notes("a1").build();
        Appointment a2 = Appointment.builder().doctor(doctor).patient(p2).initialDatetime(now.plusHours(1))
                .endDatetime(now.plusHours(1).plusMinutes(30)).notes("a2").build();
        a1 = appointmentRepository.save(a1);
        a2 = appointmentRepository.save(a2);
        final Long a1Id = a1.getId();
        final Long a2Id = a2.getId();

        // both will try to update to the same overlapping slot
        LocalDateTime targetStart = now.plusMinutes(15);
        LocalDateTime targetEnd = targetStart.plusMinutes(30);

        UpdateAppointmentDto up1 = new UpdateAppointmentDto(doctor.getId(), p1.getId(), targetStart, targetEnd, "u1");
        UpdateAppointmentDto up2 = new UpdateAppointmentDto(doctor.getId(), p2.getId(), targetStart, targetEnd, "u2");

        int threads = 2;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Callable<String>> tasks = new ArrayList<>();

        tasks.add(() -> {
            ready.countDown();
            startLatch.await();
            try {
                appointmentService.updateAppointmentById(a1Id, up1);
                return "OK1";
            } catch (ResourceAlreadyExistsException e) {
                return "CONFLICT";
            }
        });

        tasks.add(() -> {
            ready.countDown();
            startLatch.await();
            try {
                appointmentService.updateAppointmentById(a2Id, up2);
                return "OK2";
            } catch (ResourceAlreadyExistsException e) {
                return "CONFLICT";
            }
        });

        List<Future<String>> futures = new ArrayList<>();
        for (Callable<String> t : tasks)
            futures.add(exec.submit(t));

        ready.await();
        startLatch.countDown();

        int ok = 0;
        int conflict = 0;
        List<String> results = new ArrayList<>();
        for (Future<String> f : futures) {
            String r = f.get();
            results.add(r);
            if (r.startsWith("OK"))
                ok++;
            if ("CONFLICT".equals(r))
                conflict++;
        }

        exec.shutdownNow();

        assertEquals(1, ok, "Apenas uma atualização deve ser bem-sucedida");
        assertEquals(1, conflict, "Uma atualização deve falhar por conflito de horário");
    }
}
