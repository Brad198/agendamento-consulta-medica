import type { Doctor } from "./Doctor";
import type { Patient } from "./Patient";

export type Appointment = {
  id: string;
  doctor: Doctor;
  patient: Patient;
  initialDatetime: string;
  endDatetime: string;
  notes: string | null;
};
