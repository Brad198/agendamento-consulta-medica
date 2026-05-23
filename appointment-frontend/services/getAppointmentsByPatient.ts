import type { Appointment } from "@/types/entities/Appointment";
import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function getAppointmentsByPatience(
  patientId: string,
): Promise<Appointment[]> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/patients/${patientId}/appointments`,
  );
  const data = await response.json();
  if (!response.ok) {
    throw new HttpResponseError(
      "Failed to fetch appointments for patient",
      data as ResponseError,
    );
  }
  return data;
}
