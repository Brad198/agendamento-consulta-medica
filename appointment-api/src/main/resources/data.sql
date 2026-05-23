DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;
DROP SEQUENCE IF EXISTS appointments_seq;

CREATE SEQUENCE appointments_seq START WITH 1;

CREATE TABLE doctors (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    CONSTRAINT pk_doctors PRIMARY KEY (id)
);

CREATE TABLE patients (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    document_id VARCHAR(20) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    CONSTRAINT pk_patients PRIMARY KEY (id),
    CONSTRAINT uk_patients_document UNIQUE (document_id)
);

CREATE TABLE appointments (
    id BIGINT NOT NULL,
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    initial_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT pk_appointments PRIMARY KEY (id),
    CONSTRAINT fk_appointments_doctors FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_appointments_patients FOREIGN KEY (patient_id) REFERENCES patients (id)
);


INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a111-7a6f-b123-456789abcdef', 'Dr. Arnaldo Silva', 'CARDIOLOGY');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a222-7b6f-b123-456789abcdef', 'Dra. Beatriz Santos', 'PEDIATRICS');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a333-7c6f-b123-456789abcdef', 'Dr. Carlos Oliveira', 'DERMATOLOGY');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a444-7d6f-b123-456789abcdef', 'Dra. Daniela Lima', 'GYNECOLOGY');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a555-7e6f-b123-456789abcdef', 'Dr. Eduardo Souza', 'ORTHOPEDICS');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a666-7f6f-b123-456789abcdef', 'Dra. Fernanda Costa', 'NEUROLOGY');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a777-7a6f-b123-456789abcdef', 'Dr. Gabriel Martins', 'GENERAL_PRACTICE');
INSERT INTO doctors (id, name, specialty) VALUES ('018f9e61-a888-7b6f-b123-456789abcdef', 'Dra. Helena Ribeiro', 'PSYCHIATRY');

INSERT INTO patients (id, name, birth_date, document_id, phone) VALUES ('018f9e61-b111-7a6f-b123-456789abcdef', 'Rodrigo Alencar', '1988-04-15', '12345678901', '(11) 99999-1111');
INSERT INTO patients (id, name, birth_date, document_id, phone) VALUES ('018f9e61-b222-7b6f-b123-456789abcdef', 'Mariana Rocha', '1995-09-23', '23456789012', '(11) 99999-2222');
INSERT INTO patients (id, name, birth_date, document_id, phone) VALUES ('018f9e61-b333-7c6f-b123-456789abcdef', 'Juliana Mendes', '2002-12-05', '34567890123', '(11) 99999-3333');
INSERT INTO patients (id, name, birth_date, document_id, phone) VALUES ('018f9e61-b444-7d6f-b123-456789abcdef', 'Ricardo Fonseca', '1974-01-30', '45678901234', '(11) 99999-4444');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes) 
VALUES (nextval('appointments_seq'), '018f9e61-a111-7a6f-b123-456789abcdef', '018f9e61-b111-7a6f-b123-456789abcdef', '2026-08-25 14:00:00', '2026-08-25 15:00:00', 'Primeira consulta de rotina');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes)
VALUES (nextval('appointments_seq'), '018f9e61-a111-7a6f-b123-456789abcdef', '018f9e61-b111-7a6f-b123-456789abcdef', '2026-05-25 09:00:00', '2026-05-25 10:00:00', 'Consulta Geral');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes)
VALUES (nextval('appointments_seq'), '018f9e61-a222-7b6f-b123-456789abcdef', '018f9e61-b222-7b6f-b123-456789abcdef', '2026-05-26 14:30:00', '2026-05-26 15:30:00', 'Rotina Pediatria');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes)
VALUES (nextval('appointments_seq'), '018f9e61-a333-7c6f-b123-456789abcdef', '018f9e61-b333-7c6f-b123-456789abcdef', '2026-05-27 18:00:00', '2026-05-27 19:00:00', 'Avaliação de Pele');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes)
VALUES (nextval('appointments_seq'), '018f9e61-a444-7d6f-b123-456789abcdef', '018f9e61-b444-7d6f-b123-456789abcdef', '2026-05-28 08:00:00', '2026-05-28 09:00:00', 'Check-up Mulher');

INSERT INTO appointments (id, doctor_id, patient_id, initial_datetime, end_datetime, notes)
VALUES (nextval('appointments_seq'), '018f9e61-a555-7e6f-b123-456789abcdef', '018f9e61-b111-7a6f-b123-456789abcdef', '2026-05-29 16:00:00', '2026-05-29 17:00:00', 'Retorno Ortopedia');
