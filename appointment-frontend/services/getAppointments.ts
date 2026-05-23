import type { Appointment } from "@/types/entities/Appointment";
import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function getAppointments(): Promise<Appointment[]> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/appointments`,
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
