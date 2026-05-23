import type { Appointment } from "@/types/entities/Appointment";
import type { Doctor } from "@/types/entities/Doctor";
import type { Patient } from "@/types/entities/Patient";
import {
  HttpResponseError,
  ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export type CreateAppointmentDto = Omit<
  Appointment,
  "id" | "doctor" | "patient"
> & {
  doctorId: Doctor["id"];
  patientId: Patient["id"];
};

export async function createAppointment(
  appointment: CreateAppointmentDto,
): Promise<void> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/appointments`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(appointment),
    },
  );

  if (!response.ok) {
    const errorData: ResponseError = await response.json();
    throw new HttpResponseError("Failed to create appointment", errorData);
  }
}
