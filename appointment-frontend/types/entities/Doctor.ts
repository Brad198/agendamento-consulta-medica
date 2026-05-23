type Specialty =
  | "CARDIOLOGY"
  | "DERMATOLOGY"
  | "PEDIATRICS"
  | "GYNECOLOGY"
  | "ORTHOPEDICS"
  | "NEUROLOGY"
  | "PSYCHIATRY"
  | "GENERAL_PRACTICE";

export type Doctor = {
  id: string;
  name: string;
  specialty: Specialty;
};
