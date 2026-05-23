import type { Patient } from "@/types/entities/Patient";
import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function getPatientById(id: string): Promise<Patient> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/patients/${id}`,
  );
  const data = await response.json();
  if (!response.ok) {
    throw new HttpResponseError(
      "Failed to fetch patient",
      data as ResponseError,
    );
  }
  return data;
}
